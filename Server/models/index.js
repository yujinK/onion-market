const Sequelize = require('sequelize');
const env = process.env.NODE_ENV || 'development';
const config = require('../config/config')[env];
const User = require('./user');
const Sale = require('./sale');
const Category = require('./category');

const db = {};
const sequelize = new Sequelize(
  config.database, config.username, config.password, config,
);

db.sequelize = sequelize;

db.User = User;
db.Sale = Sale;
db.Category = Category;

User.init(sequelize);
Sale.init(sequelize);
Category.init(sequelize);

User.associate(db);
Sale.associate(db);
Category.associate(db);

module.exports = db;