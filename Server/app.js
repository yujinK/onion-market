const express = require('express');
const cookieParser = require('cookie-parser');
const morgan = require('morgan');
const path = require('path');
const dotenv = require('dotenv');
const passport = require('passport');
const passportConfig = require('./passport');

dotenv.config();
const userRouter = require('./routes/user');
const authRouter = require('./routes/auth');
const saleRouter = require('./routes/sale');
const categoryRouter = require('./routes/category');
const locationRouter = require('./routes/location');
const chatRouter = require('./routes/chat');
const { sequelize } = require('./models');
const webSocket = require('./socket');

const app = express();
app.set('port', process.env.PORT || 3000);

sequelize.sync({ force: false })
    .then(() => {
        console.log('데이터베이스 연결 성공');
    })
    .catch((err) => {
        console.error(err);
    });

app.use(morgan('dev'));
app.use('/img', express.static(__dirname + '/uploads'));
app.use(express.json());
app.use(express.urlencoded({ extended: false }));
app.use(cookieParser(process.env.COOKIE_SECRET));
app.use(passport.initialize());
passportConfig();   //패스포트 설정

app.use('/user', userRouter);
app.use('/auth', authRouter);
app.use('/sale', saleRouter);
app.use('/category', categoryRouter);
app.use('/location', locationRouter);
app.use('/chat', chatRouter);

app.use((req, res, next) => {
    const error = new Error(`${req.method} ${req.url} 라우터가 없습니다.`);
    error.status = 404;
    next(error);
});

app.use((err, req, res, next) => {
    res.locals.message = err.message;
    res.locals.error = process.env.NODE_ENV !== 'production' ? err : {};
    res.status(err.status || 500);
    res.json('{"code": -1, "message": ' + err.message + '}');
});

const server = app.listen(app.get('port'), () => {
    console.log(app.get('port'), '번 포트에서 대기 중');
});

webSocket(server, app);