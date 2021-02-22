const express = require('express');
const passport = require('passport');

const Favorite = require('../models/favorite');

const router = express.Router();

// 관심목록 추가된 게시글 가져오기
router.get('/', passport.authenticate('jwt', { session: false }), async (req, res) => {
   try {
       await Favorite.findAll({
           where: {
               userId: req.query.userId,
               saleId: req.query.saleId
           }
       }).then(function(result) {
           return res.status(200).json({ favorites: result })
       });
   } catch(error) {
       console.error(error);
   }
});

// 관심목록 추가하기
router.post('/', passport.authenticate('jwt', { session: false }), async (req, res) => {
    try {
        await Favorite.create({
            saleId: req.body.saleId,
            userId: req.body.userId
        }).then(function(result) {
            return res.status(201).end();
        });
    } catch(error) {
        console.error(error);
    }
});

// 관심목록 삭제하기
router.delete('/', passport.authenticate('jwt', { session: false }), async (req, res) => {
    try {
        await Favorite.destroy({
            where: {
                saleId: req.body.saleId,
                userId: req.body.userId
            }
        }).then(function(result) {
            return res.status(201).end();
        });
    } catch(error) {
        console.error(error);
    }
})

module.exports = router;