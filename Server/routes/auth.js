const express = require('express');
const passport = require('passport');
const jwt = require('jsonwebtoken');
const bcrypt = require('bcrypt');
const { isLoggedIn, isNotLoggedIn } = require('./middlewares');
const User = require('../models/user');

const router = express.Router();

router.get('/is-sign-up', isNotLoggedIn, async (req, res) => {
    try {
        const exUser = await User.findOne({ where: { email: req.query.email } });
        if (exUser) {
            return res.status(409).json({});
        }
        return res.status(200).json({});
    } catch (error) {
        console.error(error);
        next(error);
    }
});

router.post('/sign-up', isNotLoggedIn, async (req, res, next) => {
    const { email, nick, password, locationId } = req.body;
    try {
        const exUser = await User.findOne({ where: { email: email } });
        if (exUser) {
            // 이미 존재하는 사용자
            return res.status(409).end();
        }
        const hash = await bcrypt.hash(password, 12);
        await User.create({
            email: email,
            nick: nick,
            password: hash,
            img: '',
            locationId: locationId,
        });
        return res.status(201).json({
            user: [
                {
                    id: null, 
                    email: email, 
                    nick: nick, 
                    img: null,
                    locationId,
                }
            ]
        });
    } catch (error) {
        console.error(error);
        return next(error);
    }
});

router.post('/login', async (req, res, next) => {
    try {
        passport.authenticate('local', (passportError, user, info) => {
            if (passportError || !user) {
                return res.status(400).json({ message: info.reason });
            }
            req.login(user, { session: false }, (loginError) => {
                if (loginError) {
                    return res.status(400).json({ message: loginError });
                }
                const token = jwt.sign({
                    id: user.id,
                    email: user.email,
                    nick: user.nick,
                    img: user.img,
                    locationId: user.locationId
                }, process.env.JWT_SECRET);

                return res.status(201).json({ 
                    User: [{
                        id: user.id,
                        email: user.email,
                        nick: user.nick,
                        img: user.img,
                        Location: {
                            id: user.Location.id,
                            sido: user.Location.sido,
                            sigun: user.Location.sigun,
                            dongmyeon: user.Location.dongmyeon,
                            li: user.Location.li
                        }
                    }],
                    token: token 
                });
            });
        })(req, res);
    } catch (error) {
        console.error(error);
        next(error);
    }
});

router.get('/logout', isLoggedIn, (req, res) => {
    req.logout();
    req.session.destroy();
    res.json('{"code": 1, "message": "로그아웃 완료"}');
});

module.exports = router;