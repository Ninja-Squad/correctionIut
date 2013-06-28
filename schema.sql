create table tweet (id bigint primary key,
                    sender varchar(30) not null,
                    body varchar(200) not null,
                    send_date timestamp not null,
                    retweet_count integer not null);

create table daily_stats (id IDENTITY primary key,
                          day date not null,
                          sender varchar(30) not null,
                          tweets_sent integer not null);
