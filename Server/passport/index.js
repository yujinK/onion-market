const passport = require('passport');
const { Strategy: LocalStrategy } = require('passport-local');
const { ExtractJwt, Strategy: JWTStrategy } = require('passport-jwt');
const bcrypt = require('bcrypt');
const dotenv = require('dotenv');
dotenv.config();

const User = require('../models/user');
const Location = require('../models/location');

const passportConfig = { usernameField: 'email', passwordField: 'password' };

const passportVerify = async (email, password, done) => {
    try {
        const user = await User.findOne({
            include: [{
                model: Location,
            }],
            where: { email: email }
        });
        if (user) {
            const result = await bcrypt.compare(password, user.password);
            if (result) {
                // 가입된 유저
                done(null, user);
            } else {
                // 가입된 유저, 비밀번호 불일치
                done(null, false, { message: '비밀번호가 일치하지 않습니다.' });
            }
        } else {
            // 가입되지 않은 유저
            done(null, false, { message: '가입되지 않은 회원입니다.' });
        }
    } catch(error) {
        console.error(error);
        done(error);
    }
};

const JWTConfig = {
    jwtFromRequest: ExtractJwt.fromHeader('authorization'),
    secretOrKey: process.env.JWT_SECRET,
};

const JWTVerify = async (jwtPayload, done) => {
    try {
        const user = await User.findOne({ where: { id: jwtPayload.id } });
        if (user) {
            done(null, user);
            return;
        }
        done(null, false, { message: '올바르지 않은 인증정보입니다.' });
    } catch(error) {
        console.error(error);
        done(error);
    }
};

module.exports = () => {
    passport.use('local', new LocalStrategy(passportConfig, passportVerify));
    passport.use('jwt', new JWTStrategy(JWTConfig, JWTVerify));
};