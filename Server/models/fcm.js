const Sequelize = require('sequelize');

module.exports = class Fcm extends Sequelize.Model {
    static init(sequelize) {
        return super.init({
            token: {
                type: Sequelize.STRING(200),
                allowNull: false
            },
            userId: {
                type: Sequelize.INTEGER,
                allowNull: false,
                unique: true
            }
        }, {
            sequelize,
            timestamps: false,
            underscored: false,
            modelName: 'Fcm',
            tableName: 'fcm',
            paranoid: false,
            charset: 'utf8',
            collate: 'utf8_general_ci',
        });
    }

    static associate(db) {
    }
};