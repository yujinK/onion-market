const express = require('express');
const { isLoggedIn, isNotLoggedIn } = require('./middlewares');

const router = express.Router();

router.use((req, res, next) => {
    res.locals.user = req.user;
    next();
});

//프로필 보기
router.get('/profile', isLoggedIn, (req, res) => {

})

module.exports = router;