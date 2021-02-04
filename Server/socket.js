const SocketIO = require('socket.io');

module.exports = (server, app) => {
    const io = SocketIO(server, { path: '/socket.io' });
    app.set('io', io);
    const chat = io.of('/chat');

    chat.on('connection', (socket) => {
        socket.on('subscribe', (chatId) => {
            socket.join(`${chatId}`);
            console.log(`join chat: chatId - ${chatId}`);
        });

        socket.on('newMessage', (data) => {
            const messageData = JSON.parse(data);
            const chatId = messageData.chatId;
            const nick = messageData.nick;
            const profile = messageData.profile;
            const messageContent = messageData.message;
            const createdAt = messageData.createdAt;

            console.log(`[ChatId ${chatId}, nick: ${nick}] message: ${messageContent}`);

            const chatData = {
                chatId: chatId,
                nick: nick,
                profile: profile,
                message: messageContent, 
                createdAt: createdAt
            };
            socket.broadcast.to(`${chatId}`).emit('updateChat', JSON.stringify(chatData));
        });

        socket.on('disconnect', (chatId) => {
            console.log(`exit chat`);
            // socket.leave(chatId);
        });
    });


}