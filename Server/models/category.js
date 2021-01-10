const Sequelize = require('sequelize');

module.exports = class Category extends Sequelize.Model {
    static init(sequelize) {
        return super.init({
            name: {
                type: Sequelize.STRING(30),
                allowNull: false,
                unique: true,
            },
        }, {
            sequelize,
            timestamps: false,
            underscored: false,
            modelName: 'Category',
            tableName: 'categories',
            paranoid: false,
            charset: 'utf8',
            collate: 'utf8_general_ci',
        });
    }

    static associate(db) {
        db.Category.hasOne(db.Sale, { foreignKey: 'categoryId', sourceKey: 'id' });
    }
}