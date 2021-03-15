const Sequelize = require('sequelize');

module.exports = class Sale extends Sequelize.Model {
    static init(sequelize) {
        return super.init({
            title: {
                type: Sequelize.STRING(300),
                allowNull: false,
            },
            content: {
                type: Sequelize.STRING(1000),
                allowNull: true,
            },
            price: {
                type: Sequelize.INTEGER,
                allowNull: true,
            },
            priceProposal: {
                type: Sequelize.BOOLEAN,
                allowNull: false,
                defaultValue: true,
            },
            chatCount: {
                type: Sequelize.INTEGER,
                allowNull: false,
                defaultValue: 0,
            },
            favoriteCount: {
                type: Sequelize.INTEGER,
                allowNull: false,
                defaultValue: 0,
            },
            viewCount: {
                type: Sequelize.INTEGER,
                allowNull: false,
                defaultValue: 0,
            },
            state: {
                type: Sequelize.INTEGER,
                allowNull: false,
                defaultValue: 0,
            }
        }, {
            sequelize,
            timestamps: true,
            underscored: false,
            modelName: 'Sale',
            tableName: 'sales',
            paranoid: false,
            charset: 'utf8mb4',
            collate: 'utf8mb4_unicode_ci'
        });
    }

    static associate(db) {
        db.Sale.belongsTo(db.User, { foreignKey: 'writer', targetKey: 'id' });
        db.Sale.belongsTo(db.Category, { foreignKey: 'categoryId', targetKey: 'id' });
        db.Sale.hasMany(db.Image, { foreignKey: 'saleId', sourceKey: 'id' });
        db.Sale.hasOne(db.Chat, { foreignKey: 'saleId', sourceKey: 'id' });
    }
};