const Sequelize = require('sequelize');

module.exports = class User extends Sequelize.Model {
    static init(sequelize) {
        return super.init({
            email: {
                type: Sequelize.STRING(40),
                allowNull: false,
                unique: true,
            },
            nick: {
                type: Sequelize.STRING(100),
                allowNull: false,
            },
            password: {
                type: Sequelize.STRING(100), 
                allowNull: false,
            },
            img: {
                type: Sequelize.STRING(200),
                allowNull: true,
            },
        }, {
            sequelize,
            timestamps: true,
            underscored: false,
            modelName: 'User',
            tableName: 'users',
            paranoid: false,
            charset: 'utf8',
            collate: 'utf8_general_ci',
        });
    }

    static associate(db) {
        db.User.hasMany(db.Sale, { foreignKey: 'writer', sourceKey: 'id' });
        db.User.belongsTo(db.Location, { foreignKey: 'locationId', targetKey: 'id' });
        db.User.hasOne(db.Chat, { foreignKey: 'buyUserId', sourceKey: 'id' });
        db.User.hasOne(db.Message, { foreignKey: 'userId', sourceKey: 'id' });
    }
};