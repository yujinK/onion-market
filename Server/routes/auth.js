const express = require('express');
const passport = require('passport');
const bcrypt = require('bcrypt');
const { isLoggedIn, isNotLoggedIn } = require('./middlewares');
const User = require('../models/user');

const router = express.Router();

router.post('/signup', isNotLoggedIn, async (req, res, next) => {
    const { email, nick, password } = req.body;
    try {
        const exUser = await User.findOne({ where: { email: email } });
        if (exUser) {
            return res.json('{"code": -1, "message": "이미 존재하는 사용자입니다.", "data": null');
        }
        const hash = await bcrypt.hash(password, 12);
        await User.create({
            email,
            nick,
            password: hash,
        });
        return res.json(201, {"user": [{"id": null, "email": email, "nick": nick, "img": null}]});
    } catch (error) {
        console.error(error);
        return next(error);
    }
});

router.post('/login', isNotLoggedIn, (req, res, next) => {
    passport.authenticate('local', (authError, user, info) => {
        if (authError) {
            console.error(authError);
            return next(authError);
        }
        if (!user) {
            return res.status(404);
        }
        return req.login(user, (loginError) => {
            if (loginError) {
                console.error(loginError);
                return next(loginError);
            }
            return res.json('{"code": 1, "message": "로그인 완료"}');
        });
    })(req, res, next);
});

router.get('/logout', isLoggedIn, (req, res) => {
    req.logout();
    req.session.destroy();
    res.json('{"code": 1, "message": "로그아웃 완료"}');
});

module.exports = router;