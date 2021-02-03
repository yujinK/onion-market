const SocketIO = require('socket.io');

module.exports = (server, app) => {
    const io = SocketIO(server, { path: '/socket.io' });
    app.set('io', io);
    const chat = io.of('/chat');

    chat.on('connection', (socket) => {
        console.log('chat 네임스페이스에 접속');
        const chatId = socket.request.chatId;
        socket.join(chatId);

        socket.on('chat', (msg) => {
            console.log("Message: " + msg);
        });

        socket.on('disconnect', () => {
            console.log('chat 네임스페이스 접속 해제');
            socket.leave(chatId);
        });
    });


}