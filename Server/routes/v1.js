const express = require('express');
const jwt = require('jsonwebtoken');

const { verifyToken } = require('./middlewares');
const { User } = require('../models/user');

const router = express.Router();

router.post('/token', async (req, res) => {
    const { clientSecret } = req.body;
    try {
        const domain = await User.findOne({
            where: { cli },
        })
    } catch (error) {
        console.error(error);
        next(error);
    }
});

module.exports = router;