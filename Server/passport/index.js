const passport = require('passport');
const { Strategy: LocalStrategy } = require('passport-local');
const bcrypt = require('bcrypt');

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

module.exports = () => {
    passport.use('local', new LocalStrategy(passportConfig, passportVerify));
};