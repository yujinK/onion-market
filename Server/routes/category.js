const express = require('express'); 
const { isLoggedIn, isNotLoggedIn } = require('./middlewares');
const Category = require('../models/category');

const router = express.Router();

router.use((req, res, next) => {
    res.locals.user = req.user;
    next();
});

router.get('/', isLoggedIn, async (req, res, next) => {
    try {
        const categories = await Category.findAll();
        console.log(JSON.stringify(categories));
        // res.status(200).json(categories);
    } catch (error) {
        console.error(error);
        next(error);
    }
});

module.exports = router;