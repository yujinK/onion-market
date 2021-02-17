const express = require('express');
const passport = require('passport');
const multer = require('multer');
const path = require('path');
const fs = require('fs');
const async = require('async');

const User = require('../models/user');
const Sale = require('../models/sale');
const Image = require('../models/image');
const Location = require('../models/location');
const Category = require('../models/category');
const { sequelize } = require('../models');

const router = express.Router();

try {
    fs.readdirSync('uploads');
} catch (error) {
    console.error('uploads 폴더가 없어 uploads 폴더를 생성합니다.');
    fs.mkdirSync('uploads');
}

const upload = multer({
    storage: multer.diskStorage({
        destination(req, file, cb) {
            cb(null, 'uploads/');
        },
        filename(req, file, cb) {
            const ext = path.extname(file.originalname);
            cb(null, path.basename(file.originalname, ext) + Date.now() + ext);
        },
    }),
    limits: { fileSize: 5 * 1024 * 1024 },
});

// 지역 게시글 가져오기
router.get('/location/:locationId', async (req, res) => {
    try {
        const sales = await Sale.findAll({
            include: [
                {
                    model: User,
                    required: true,
                    where: {
                        locationId: req.params.locationId
                    },
                    include: [{
                        model: Location,
                        where: {
                            id: req.params.locationId
                        }
                    }]
                },
                {
                    model: Category
                },
                {
                    model: Image
                }
            ],
            where: { state: req.query.state },
            order: [
                ['id', 'DESC']
            ]
        });
        return res.status(200).json({ sales });
    } catch(error) {
        console.error(error);
    }
});

// 유저 판매정보 가져오기
router.get('/user/:userId', async (req, res) => {
    try {
        const sales = await Sale.findAll({
            include: [
                {
                    model: User,
                    required: true,
                    where: {
                        id: req.params.userId
                    },
                    include: [{
                        model: Location
                    }]
                },
                {
                    model: Category
                },
                {
                    model: Image
                }
            ],
            where: { state: req.query.state },
            order: [
                ['id', 'DESC']
            ]
        });
        return res.status(200).json({ sales });
    } catch(error) {
        console.error(error);
    }
});

// 해당 id 게시글 가져오기
router.get('/id/:saleId', async (req, res) => {
    try {
        await Sale.findAll({
            include: [
                {
                    model: Image
                }
            ],
            where: { id: req.params.saleId }
        }).then(function (result) {
            return res.status(200).json({ sales: result });
        });
    } catch (error) {
        console.error(error);
    }
});

// 게시글 쓰기
router.post('/write', passport.authenticate('jwt', { session: false }), async (req, res) => {
    const { title, content, price, priceProposal, writer, categoryId } = req.body;
    try {
        await Sale.create({
            title: title,
            content: content,
            price: price,
            priceProposal: priceProposal,
            writer: writer,
            categoryId: categoryId 
        }).then(function(result) {
            return res.status(201).json({ id: result.id });
        });
    } catch(error) {
        console.error(error);
    }
});

// 게시글 이미지 서버 저장
router.post('/upload/image', passport.authenticate('jwt', { session: false }), upload.array('img', 10), (req, res) => {
    return res.status(201).json({"upload": req.files});
});

// 게시글 이미지 DB 저장
router.post('/write/image', passport.authenticate('jwt', { session: false }), async (req, res) => {
    const t = await sequelize.transaction();
    try {
        await Image.destroy({
            where: { saleId: req.body.saleId }
        }, { transaction: t });

        if (req.body.count <= 1) {
            await Image.create({
                path: req.body.images.toString(),
                priority: 0,
                saleId: req.body.saleId
            }, { transaction: t });
        } else {
            for (var i=0; i<req.body.count; i++) {
                await Image.create({
                    path: req.body.images[i],
                    priority: i,
                    saleId: req.body.saleId
                }, { transaction: t })
            }
        }        

        await t.commit().then(function(result) {
            return res.status(201).end();
        });
    } catch(error) {
        await t.rollback();
        console.error(error);
    }
});

// 게시글 삭제
router.post('/delete', passport.authenticate('jwt', { session: false }), async (req, res) => {
    try {
        await Sale.destroy({
            where: { id: req.body.id }
        }).then(function(result) {
            return res.status(201).end();
        });
    } catch(error) {
        console.error(error);
    }
});

// 게시글 이미지 삭제
router.post('/delete/image', passport.authenticate('jwt', { session: false }), async (req, res) => {
    try {
        await Image.destroy({
            where: { 
                path: req.body.path
            }
        }).then(function(result) {
            return res.status(201).end();
        });
    } catch(error) {
        console.error(error);
    }
});

// 게시글 수정
router.post('/edit', passport.authenticate('jwt', { session: false }), async (req, res) => {
    try {
        await Sale.update(
            { 
                title: req.body.title, 
                content: req.body.content,
                price: req.body.price,
                priceProposal: req.body.priceProposal,
                categoryId: req.body.categoryId
            },
            {
                where: {
                    id: req.body.id
                }
            }
        ).then(function(result) {
            return res.status(201).json({ id: result.id });
        });
    } catch(error) {
        console.error(error);
    }
})

module.exports = router;