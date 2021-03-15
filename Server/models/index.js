const Sequelize = require('sequelize');
const env = process.env.NODE_ENV || 'development';
const config = require('../config/config')[env];
const User = require('./user');
const Sale = require('./sale');
const Category = require('./category');
const Location = require('./location');
const Image = require('./image');
const Chat = require('./chat');
const Message = require('./message');
const Fcm = require('./fcm');
const Favorite = require('./favorite');

const db = {};
const sequelize = new Sequelize(
  config.database, config.username, config.password, config,
);

db.sequelize = sequelize;

db.User = User;
db.Sale = Sale;
db.Category = Category;
db.Location = Location;
db.Image = Image;
db.Chat = Chat;
db.Message = Message;
db.Fcm = Fcm;
db.Favorite = Favorite;

User.init(sequelize);
Sale.init(sequelize);
Category.init(sequelize);
Location.init(sequelize);
Image.init(sequelize);
Chat.init(sequelize);
Message.init(sequelize);
Fcm.init(sequelize);
Favorite.init(sequelize);

User.associate(db);
Sale.associate(db);
Category.associate(db);
Location.associate(db);
Image.associate(db);
Chat.associate(db);
Message.associate(db);
Fcm.associate(db);
Favorite.associate(db);

module.exports = db;