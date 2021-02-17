const Sequelize = require('sequelize');

module.exports = class Chat extends Sequelize.Model {
    static init(sequelize) {
        return super.init({
            lastMessage: {
                type: Sequelize.STRING(500),
                allowNull: true,
            }
        }, {
            sequelize,
            timestamps: true,
            underscored: false,
            modelName: 'Chat',
            tableName: 'Chats',
            paranoid: false,
            charset: 'utf8mb4',
            collate: 'utf8mb4_unicode_ci'
        });
    }

    static associate(db) {
        db.Chat.hasMany(db.Message, { foreignKey: 'chatId', sourceKey: 'id' });
        db.Chat.belongsTo(db.Sale, { foreignKey: 'saleId', targetKey: 'id' });
        db.Chat.belongsTo(db.User, { foreignKey: 'buyUserId', targetKey: 'id' });
    }
};