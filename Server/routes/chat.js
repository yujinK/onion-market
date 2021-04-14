const express = require('express');
const passport = require('passport');
const admin = require('firebase-admin');

const Chat = require('../models/chat');
const Message = require('../models/message');
const Sale = require('../models/sale');
const Image = require('../models/image');
const User = require('../models/user');
const Location = require('../models/location');
const Fcm = require('../models/fcm');
const { sequelize } = require('../models');
const { Op, Sequelize } = require('sequelize');

const router = express.Router();

admin.initializeApp({
    credential: admin.credential.applicationDefault()
});

// 기존 채팅 있는지 확인 (판매자))
router.get('/existing-sale-chat', passport.authenticate('jwt', { session: false }), async (req, res, next) => {
    try {
        const chat = await Chat.findAll({
            include: [
                {
                    model: User,
                    require: true,
                    include: [{
                        model: Location
                    }]
                }
            ],
            where: {
                saleId: req.query.saleId
            }
        }).then(function (chat) {
            return res.status(200).json({ chats: chat })
        });
    } catch (error) {
        console.error(error);
    }
});

// 기존 채팅 있는지 확인 (구매자)
router.get('/existing-buy-chat', passport.authenticate('jwt', { session: false }), async (req, res, next) => {
    try {
        const chat = await Chat.findOne({
            where: {
                buyUserId: req.query.userId,
                saleId: req.query.saleId
            }
        }).then(function (chat) {
            if (chat == null) {
                return res.status(200).json({ chatId: -1 });
            } else {
                return res.status(200).json({ chatId: chat.id });
            }
        });
    } catch (error) {
        console.error(error);
    }
});

// 새로운 채팅 시작
router.post('/new-chat', passport.authenticate('jwt', { session: false }), async (req, res, next) => {
    const t = await sequelize.transaction();
    try {
        const newChat = await Chat.create({
            lastMessage: "",
            buyUserId: req.body.buyUserId,
            saleId: req.body.saleId
        }, { transaction: t });

        await sequelize.query(
            `UPDATE sales SET chatCount = (SELECT COUNT(*) FROM chats WHERE saleId = ${req.body.saleId}) WHERE id = ${req.body.saleId}`,
            { transaction: t }
        );

        await t.commit().then(function (result) {
            return res.status(201).json({ chatId: newChat.id });
        });
    } catch (error) {
        console.error(error);
    }
});


// 유저 채팅 모두 가져오기
router.get('/user/:userId', passport.authenticate('jwt', { session: false }), async (req, res, next) => {
    try {
        await Chat.findAll({
            include: [
                {
                    model: User,
                    required: true,
                    include: [
                        {
                            model: Location,
                            required: true
                        }
                    ]
                },
                {
                    model: Sale,
                    required: true,
                    include: [
                        {
                            model: User,
                            required: true,
                            include: [
                                {
                                    model: Location,
                                    required: true
                                }
                            ]
                        },
                        {
                            model: Image,
                            required: true
                        }
                    ]
                }
            ],
            where: {
                [Op.or]: [
                    { '$Sale.writer$': req.params.userId },
                    { buyUserId: req.params.userId }
                ]
            },
            order: [
                [ 'updatedAt', 'DESC' ]
            ]
        }).then(function (result) {
            return res.status(200).json({ chats: result });
        });
    } catch (error) {
        console.error(error);
    }
});

// 기존 채팅 불러오기
router.get('/load/:chatId', passport.authenticate('jwt', { session: false }), async (req, res, next) => {
    try {
        await Message.findAll({
            include: [
                {
                    model: User,
                    required: true
                }
            ],
            where: { 
                chatId: req.params.chatId
            },
            order: [
                ['createdAt', 'ASC']
            ]
        }).then(function(result) {
            return res.status(200).json({ messages: result });
        });
    } catch (error) {
        console.error(error);
    }
});

// 채팅
router.post('/send/:chatId', passport.authenticate('jwt', { session: false }), async (req, res, next) => {
    const t = await sequelize.transaction();
    try {
        await Chat.update(
            { lastMessage: req.body.message },
            { where: { id: req.params.chatId } }
        , { transaction: t });
        
        await Message.create({
            message: req.body.message,
            userId: req.body.userId,
            chatId: req.params.chatId
        }, { transaction: t });
        
        await t.commit().then(function (result) {
            sequelize.query(`SELECT fcm.token, users.nick FROM fcm
                             INNER JOIN users ON fcm.userId = users.id
                             WHERE userId = (SELECT IF(chats.buyUserId = ${req.body.userId}, sales.writer, chats.buyUserId) AS userId 
                             FROM chats 
                             INNER JOIN sales ON chats.saleId = sales.id)`, 
                            { type: Sequelize.QueryTypes.SELECT })
                            .then(function(result) {
                                notification(result[0].token, result[0].nick, req.params.chatId, req.body.message, req.body.saleId);
                            });
            
            return res.status(201).end();
        });
    } catch (error) {
        await t.rollback();
        console.error(error);
    }
});

// 채팅 알림
function notification(token, nick, chatId, message, saleId) {
    console.log(`FCM: ${token}`);
    var noti = {
        data: {
            'chatId': chatId,
            'nick': nick,
            'message': message,
            'saleId': saleId
        },
        token: token
    };

    admin.messaging().send(noti)
        .then((response) => {
            console.log('Successfully sent message: ', response);
        })
        .catch((error) => {
            console.log('Error sending message: ', error);
        });
}

// 빈 채팅방 삭제
router.delete('/delete/:chatId', passport.authenticate('jwt', { session: false }), async (req, res, next) => {
    const t = await sequelize.transaction();
    try {
        await Chat.destroy({
            where: { id: req.params.chatId }
        }, { transaction: t });

        await sequelize.query(
            `UPDATE sales SET chatCount = (SELECT COUNT(*) FROM chats WHERE saleId = ${req.body.saleId}) WHERE id = ${req.body.saleId}`,
            { transaction: t }
        );

        await t.commit().then(function (result) {
            return res.status(200).end();
        });
    } catch (error) {
        console.error(error);
    }
});

module.exports = router;

