const Sequelize = require('sequelize');
const env = process.env.NODE_ENV || 'development';
const config = require('../config/config')[env];
const User = require('./user');
const Sale = require('./sale');
const Category = require('./category');
const Location = require('./location');

const db = {};
const sequelize = new Sequelize(
  config.database, config.username, config.password, config,
);

db.sequelize = sequelize;

db.User = User;
db.Sale = Sale;
db.Category = Category;
db.Location = Location;

User.init(sequelize);
Sale.init(sequelize);
Category.init(sequelize);
Location.init(sequelize);

User.associate(db);
Sale.associate(db);
Category.associate(db);
Location.associate(db);

module.exports = db;