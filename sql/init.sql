
DROP DATABASE IF EXISTS `bugtrack`;
CREATE DATABASE `bugtrack`;
USE `bugtrack`;

DROP TABLE IF EXISTS `instance`;
CREATE TABLE `instance` (
    `id` int UNSIGNED NOT NULL AUTO_INCREMENT,
    `commit_id` int NOT NULL,
    `severity` varchar(8) NOT NULL,
    `type` varchar(10) NOT NULL,
    `status` varchar(10) NOT NULL,
    `author` varchar(40) NOT NULL,
    `message` varchar(200) NOT NULL,
    `creation_date` varchar(24) NOT NULL,
    `update_date` varchar(24) NOT NULL,
    PRIMARY KEY (`id`)
);

DROP TABLE IF EXISTS `commit`;
CREATE TABLE `commit` (
    `id` int UNSIGNED NOT NULL AUTO_INCREMENT,
    `commit_hash` varchar(30) NOT NULL,
    `branch` varchar(30) NOT NULL,
    `repository` varchar(30) NOT NULL,
    `committer` varchar(50) NOT NULL,
    `commit_time` varchar(24) NOT NULL,
    primary key (`id`)
);

DROP TABLE IF EXISTS `location`;
CREATE TABLE `location` (
    `component` varchar(200) NOT NULL,
    `start_line` int NOT NULL,
    `end_line` int NOT NULL,
    `start_offset` int NOT NULL,
    `end_offset` int NOT NULL,
    `inst_id` int NOT NULL,
    primary key (`component`, start_line, end_line, start_offset, end_offset, inst_id)
);

DROP TABLE IF EXISTS `match`;
CREATE TABLE `match` (
    `parent_id` int NOT NULL,
    `child_id` int NOT NULL,
    primary key (`parent_id`, child_id)
);

DROP TABLE IF EXISTS `instcase`;
CREATE TABLE `instcase` (
    `id` int UNSIGNED NOT NULL AUTO_INCREMENT,
    `status` varchar(7) NOT NULL,
    `type` varchar(10) NOT NULL,
    `commit_new` varchar(30) NOT NULL,
    `commit_last` varchar(30) NOT NULL,
    `create_time` varchar(24) NOT NULL,
    `update_time` varchar(24) NOT NULL,
    `committer_new` varchar(50) NOT NULL,
    `committer_last` varchar(50) NOT NULL,
    primary key (`id`)
);