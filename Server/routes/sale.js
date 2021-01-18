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
const { response } = require('express');

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
            cb(null, "OM-" + path.basename(file.originalname, ext) + Date.now() + ext);
        },
    }),
    limits: { fileSize: 5 * 1024 * 1024 },
});

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
        next(error);
    }
});

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
        next(error);
    }
});

router.post('/write', passport.authenticate('jwt', { session: false }), async (req, res, next) => {
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
        next(error);
    }
});

router.post('/write/image', passport.authenticate('jwt', { session: false }), upload.array('img', 10), async (req, res, next) => {
    try {
        for (var i=0; i<req.files.length; i++) {
            await Image.create({
                path: req.files[i].filename,
                priority: i,
                saleId: req.query.saleId
            });
        }
        return res.status(201).end();
    } catch(error) {
        console.error(error);
        next(error);
    }
});

// router.get('/thumbnail/:saleId', async (req, res) => {
//     try {
//         var thumbnail = await Image.findOne({
//             where: {
//                 saleId: req.params.saleId,
//                 priority: 0
//             }
//         });

//         var filePath = "uploads/" + thumbnail.path;
//         fs.readFile(filePath, function (err, data) {
//             if(!err) {
//                 return res.status(200).send(data);
//             } else {
//                 console.error(err);
//             }
//         })
//     } catch(error) {
//         console.error(error);
//     }
// });

// router.get('/image/:saleId', async (req, res) => {
//     try {
//         await Image.findAll({
//             where: {
//                 saleId: req.params.saleId
//             },
//             order: [
//                 ['priority', 'ASC']
//             ]
//         }).then(function(result) {
//             let images = [];
//             var promises = result.map(function(_path) {
//                 return new Promise(function(_path, resolve, reject) {
//                     fs.readFile("uploads/" + _path.path, function(err, data) {
//                         if (err) {
//                             console.error(err);
//                         }
//                     });
//                 }).then((image) => {
//                     images.push(image);
//                 });
//             });
    
//             Promise.all(promises).then(() => {
//                 res.status(200).json(images);
//             });
//         });
//     } catch(error) {
//         console.error(error);
//     }
// });

// router.get('/image/:filename', (req, res) => {
//     var filePath = "uploads/" + req.params.filename;
//     fs.readFile(filepa)
// });

router.post('/delete', passport.authenticate('jwt', { session: false }), async (req, res, next) => {
    try {
        await Sale.destroy({
            where: { id: req.body.id }
        }).then(function(result) {
            return res.status(201).end();
        });
    } catch(error) {
        console.error(error);
        next(error);
    }
});

module.exports = router;