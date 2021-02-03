const Sequelize = require('sequelize');

module.exports = class Message extends Sequelize.Model {
    static init(sequelize) {
        return super.init({
            message: {
                type: Sequelize.STRING(500),
                allowNull: false,
            }
        }, {
            sequelize,
            timestamps: true,
            underscored: false,
            modelName: 'Message',
            tableName: 'Messages',
            paranoid: false,
            charset: 'utf8mb4',
            collate: 'utf8mb4_unicode_ci'
        });
    }

    static associate(db) {
        db.Message.belongsTo(db.Chat, { foreignKey: 'chatId', targetKey: 'id' });
        db.Message.belongsTo(db.User, { foreignKey: 'userId', targetKey: 'id' });
    }
}