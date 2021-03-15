const Sequelize = require('sequelize');

module.exports = class Image extends Sequelize.Model {
    static init(sequelize) {
        return super.init({
            path: {
                type: Sequelize.STRING(300),
                allowNull: false
            },
            priority: {
                type: Sequelize.INTEGER,
                allowNull: false,
                defaultValue: 0
            }
        }, {
            sequelize,
            timestamps: false,
            underscored: false,
            modelName: 'Image',
            tableName: 'images',
            paranoid: false,
            charset: 'utf8',
            collate: 'utf8_general_ci'
        });
    }

    static associate(db) {
        db.Image.belongsTo(db.Sale, { foreignKey: 'saleId', targetKey: 'id' });
    }
};