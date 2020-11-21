# set range of ids of subjects
alter table video_movie
    auto_increment = 10000000;
alter table video_series
    auto_increment = 30000000;
alter table video_season
    auto_increment = 40000000;
alter table video_episode
    auto_increment = 60000000;