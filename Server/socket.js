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
            const id = messageData.id;
            const messageContent = messageData.message;
            const createdAt = messageData.createdAt;
            const user = messageData.User;
            const chatId = messageData.chatId;

            console.log(`[ChatId ${chatId}, nick: ${user.nick}] message: ${messageContent}`);

            const chatData = {
                id: id,
                message: messageContent,
                createdAt: createdAt,
                User: user, 
                chatId: chatId
            };
            socket.broadcast.to(`${chatId}`).emit('updateChat', JSON.stringify(chatData));
        });

        socket.on('disconnect', (chatId) => {
            console.log(`exit chat`);
            socket.leave(chatId);
        });
    });


}