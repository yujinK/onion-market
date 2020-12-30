const Sequelize = require('sequelize');

module.exports = class Location extends Sequelize.Model {
    static init(sequelize) {
        return super.init({
            sido: {
                type: Sequelize.STRING(30),
                allowNull: false,
            },
            sigun: {
                type: Sequelize.STRING(30),
                allowNull: false,
            },
            dongmyeon: {
                type: Sequelize.STRING(30),
                allowNull: false,
            },
            li: {
                type: Sequelize.STRING(30),
                allowNull: true,
            },
        }, {
            sequelize,
            timestamps: true,
            underscored: false,
            modelName: 'Location',
            tableName: 'locations',
            paranoid: true,
            charset: 'utf8',
            collate: 'utf8_general_ci',
        })
    }

    static associate(db) {
        
    }
}