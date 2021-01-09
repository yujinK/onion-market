const express = require('express');
const passport = require('passport');
const multer = require('multer');
const path = require('path');
const fs = require('fs');

const Sale = require('../models/sale');
const Image = require('../models/image');
const { isLoggedIn } = require('./middlewares');
const { route } = require('./auth');

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

router.post('/write/image', passport.authenticate('jwt', { session: false }), upload.single('img'), async (req, res, next) => {
    try {
        await Image.create({
            path: req.file.filename,
            priority: req.query.priority,
            saleId: req.query.saleId
        });
        return res.status(201);
    } catch(error) {
        console.error(error);
        next(error);
    }
});



// router.post('/img', isLoggedIn, upload.single('img'), (req, res) => {
//     console.log(req.file);
//     res.json({ url: `/img/${req.file.filename}` });
// });

// const upload2 = multer();
// router.post('/', isLoggedIn, upload2.none(), async (req, res, next) => {
//     try {
//         const post = await Post.create({
//             title: req.body.title,
//             content: req.body.content,
//             price: req.body.price,
//             priceProposal: req.body.priceProposal,
//             writer: req.user.id,
//         });

//         res.status(201).json({});   //TODO: json 수정
//     } catch (error) {
//         console.error(error);
//         next(error);
//     }
// });

module.exports = router;