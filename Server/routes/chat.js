const express = require('express');
const passport = require('passport');

const Chat = require('../models/chat');
const Message = require('../models/message');
const Sale = require('../models/sale');
const Image = require('../models/image');
const User = require('../models/user');
const Location = require('../models/location');
const { sequelize } = require('../models');
const { Op, Sequelize } = require('sequelize');

const router = express.Router();

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
    try {
        const newChat = await Chat.create({
            lastMessage: "",
            buyUserId: req.body.buyUserId,
            saleId: req.body.saleId
        });
        return res.status(201).json({ chatId: newChat.id });
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
            }
        }).then(function (result) {
            return res.status(200).json({ chats: result });
        });
    } catch (error) {
        console.error(error);
    }
});

// router.get('/user/:userId', passport.authenticate('jwt', { session: false }), async (req, res, next) => {
//     try {
//         const query = "SELECT `Chat`.`id`, `Chat`.`lastMessage`, `Chat`.`createdAt`, `Chat`.`updatedAt`, `Sale`.`id`, `Sale`.`title`, `Sale`.`content`, `Sale`.`price`, `Sale`.`priceProposal`, `Sale`.`chatCount`, `Sale`.`favoriteCount`, `Sale`.`viewCount`, `Sale`.`state`, `Sale`.`createdAt`, `Sale`.`updatedAt`, `Sale`.`deletedAt`, `Sale`.`writer`, `Sale`.`categoryId`, `BuyUser`.`id`, `BuyUser`.`email`, `BuyUser`.`nick`, `BuyUser`.`img`, `BuyUser->Location`.`id` AS `BuyUser.Location.id`, `BuyUser->Location`.`sido` AS `BuyUser.Location.sido`, `BuyUser->Location`.`sigun` AS `BuyUser.Location.sigun`, `BuyUser->Location`.`dongmyeon` AS `BuyUser.Location.dongmyeon`, `BuyUser->Location`.`li` AS `BuyUser.Location.li`, `SaleUser`.`id`, `SaleUser`.`email`, `SaleUser`.`nick`, `SaleUser`.`img`, `SaleUser->Location`.`id` AS `SaleUser.Location.id`, `SaleUser->Location`.`sido` AS `SaleUser.Location.sido`, `SaleUser->Location`.`sigun` AS `SaleUser.Location.sigun`, `SaleUser->Location`.`dongmyeon` AS `SaleUser.Location.dongmyeon`, `SaleUser->Location`.`li` AS `SaleUser.Location.li` FROM chats AS `Chat` INNER JOIN users AS `BuyUser` ON `Chat`.`buyUserId` = `BuyUser`.`id` INNER JOIN sales AS `Sale` ON `Chat`.`saleId` = `Sale`.`id` INNER JOIN users AS `SaleUser` ON `Sale`.`writer` = `SaleUser`.`id` INNER JOIN locations AS `BuyUser->Location` ON `BuyUser`.`locationId` = `BuyUser->Location`.`id` INNER JOIN locations AS `SaleUser->Location` ON `SaleUser`.`locationId` = `SaleUser->Location`.`id` WHERE `Chat`.`buyUserId` = " + req.params.userId + " OR `Sale`.`writer` = " + req.params.userId + " ORDER BY `Chat`.`updatedAt` DESC";
//         await sequelize.query(query,
//              { type: Sequelize.QueryTypes.SELECT }
//         ).then(function (result) {
//             return res.status(200).json({ chats: result });
//         });
//     } catch (error) {
//         console.error(error);
//     }
// });


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
            return res.status(201).end();
        });
    } catch (error) {
        await t.rollback();
        console.error(error);
    }
});

// 빈 채팅방 삭제
router.delete('/delete/:chatId', passport.authenticate('jwt', { session: false }), async (req, res, next) => {
    try {
        await Chat.destroy({
            where: { id: req.params.chatId }
        }).then(function (result) {
            return res.status(200).end();
        })
    } catch (error) {
        console.error(error);
    }
});

module.exports = router;

