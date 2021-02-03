const express = require('express');
const passport = require('passport');

const Chat = require('../models/chat');
const Message = require('../models/message');
const Sale = require('../models/sale');
const User = require('../models/user');
const { sequelize } = require('../models');

const router = express.Router();

// 기존 채팅 있는지 확인
router.get('/existingChat', passport.authenticate('jwt', { session: false }), async (req, res, next) => {
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
router.post('/newChat', passport.authenticate('jwt', { session: false }), async (req, res, next) => {
    const t = await sequelize.transaction();
    try {
        const newChat = await Chat.create({
            lastMessage: req.body.message,
            buyUserId: req.body.buyUserId,
            saleId: req.body.saleId
        }, { transaction: t });
        const message = await Message.create({
            message: req.body.message,
            chatId: newChat.id,
            userId: req.body.buyUserId
        }, { transaction: t });
        await t.commit();
        
        req.app.get('io').to(newChat.id).emit('chat', message);
        return res.status(201).json({ chatId: newChat.id });
    } catch (error) {
        console.error(error);
    }
});

// 유저 채팅 모두 가져오기
router.get('/user/:userId', passport.authenticate('jwt', { session: false }), async (req, res, next) => {
    try {
        const chat = await Chat.findAll({
            include: [
                {
                    model: Sale,
                    required: true,
                    where: {
                        id: req.params.userId
                    },
                    include: [{
                        mode: User
                    }]
                }
            ],
            where: {
                [Op.or]: [
                    { buyUserId: req.params.userId },
                    { writer: req.params.userId }
                ]
            },
            order: [
                ['updatedAt', 'DESC']
            ]
        }).then(function(result) {
            return res.status(200).json(result);
        });
    } catch (error) {
        console.error(error);
    }
});


// 기존 채팅 불러오기
router.get('/load/:chatId', passport.authenticate('jwt', { session: false }), async (req, res, next) => {
    try {
        const messages = await Message.findAll({
            include: [
                {
                    model: User,
                    required: true
                }
            ],
            where: { 
                id: req.params.chatId
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
    try {
        const message = await Message.create({
            message: req.body.message,
            userId: req.body.userId,
            chatId: req.params.chatId
        });
        req.app.get('io').of('/chat').to(req.params.chatId).emit('message', message);
    } catch (error) {
        console.error(error);
    }
});

module.exports = router;

