const express = require('express');
const Location = require('../models/location');
const { Op, json } = require('sequelize');
const { isNotLoggedIn } = require('./middlewares');

const router = express.Router();

router.get('/', async (req, res, next) => {
    try {
        const locationSet = await Location.findAll({
            raw: true
        });
        console.log(JSON.parse(JSON.stringify(locationSet)));
        return res.status(200).json(JSON.parse(JSON.stringify(locationSet)));
        // console.log(json(locationSet));
        // return res.status(200).json(locationSet);
    } catch (error) {
        console.error(error);
        next(error);
    }
});

module.exports = router;