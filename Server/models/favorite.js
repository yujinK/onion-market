const Sequelize = require('sequelize');

module.exports = class Favorite extends Sequelize.Model {
    static init(sequelize) {
        return super.init({
            saleId: {
                type: Sequelize.INTEGER,
                allowNull: false
            },
            userId: {
                type: Sequelize.INTEGER,
                allowNull: false
            }
        }, {
            sequelize,
            timestamps: false,
            underscored: false,
            modelName: 'Favorite',
            tableName: 'Favorites',
            paranoid: false
        });
    }

    static associate(db) {
    }
};