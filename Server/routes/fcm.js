const express = require('express');
const passport = require('passport');
const { sequelize } = require('../models');

const Fcm = require('../models/fcm');

const router = express.Router();

// Firebase Token 저장
router.post('/:userId', passport.authenticate('jwt', { session: false }), async (req, res) => {
    try {
        await Fcm.upsert({
            token: req.body.fcm,
            userId: req.params.userId
        }).then(function (result) {
            return res.status(201).end();
        });
    } catch (error) {
        console.error(error);
    }
});

module.exports = router;