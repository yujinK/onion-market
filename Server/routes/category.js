const express = require('express');
const passport = require('passport');
const Category = require('../models/category');

const router = express.Router();

router.use((req, res, next) => {
    res.locals.user = req.user;
    next();
});

router.get('/', passport.authenticate('jwt', { session: false }), async (req, res, next) => {
    try {
        const categories = await Category.findAll({
            order: ['id']
        });
        return res.status(200).json({ category: categories })
    } catch (error) {
        console.error(error);
        next(error);
    }
});

module.exports = router;