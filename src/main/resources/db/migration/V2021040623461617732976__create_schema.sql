DROP TABLE IF EXISTS `report_label`;
CREATE TABLE `report_label` (
  `id` int unsigned NOT NULL AUTO_INCREMENT,
  `label_name` varchar(50) NOT NULL,
  `label_value` varchar(2500) NOT NULL,
  PRIMARY KEY (`id`)
);

insert into `report_label` (`label_name`, `label_value`) values ('customer_id', 'Customer Id');
insert into `report_label` (`label_name`, `label_value`) values ('customer_name', 'Customer Name');
insert into `report_label` (`label_name`, `label_value`) values ('phone_number', 'Phone Number');