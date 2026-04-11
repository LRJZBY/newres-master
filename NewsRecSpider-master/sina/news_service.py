import random

from models.userRecommenderList import UserRecommenderList
from models.news import News
from models.user import User
from recall_module import *


def generate_news_embedding(titles_obj):
    titles = []
    news_utils: NewsUtils = get_val(NEWS_UTILS)
    
    if titles_obj is None:
        # 自动查询数据库中所有未生成向量的新闻
        unembedded_news = News.objects(embedding__exists=False)
        titles_obj = [
            {"id": str(news.id), "title": news.title, "createTime": news.create_time} 
            for news in unembedded_news
        ]
    
    for title_obj in titles_obj:
        titles.append(title_obj["title"])
    title_embedding = news_utils.generate_title_embedding(titles)
    for index in range(len(titles)):
        news_embedding = NewsEmbedding()
        news_embedding["doc_id"] = titles_obj[index]["id"]
        news_embedding["embedding"] = title_embedding[index]
        news_embedding["create_time"] = titles_obj[index]["createTime"]
        news_embedding.save()


def load_news_embedding_by_K(user_history, k=7):
    """查询最近七天的新闻embedding数据，去除掉用户已看过的新闻"""
    last_time = int(time.time() - 24 * 3600 * k)
    recent_news = list(NewsEmbedding.objects(create_time__gt=last_time))
    # 去掉用户看过的新闻
    final_news = []
    user_history_news_id_set = set(user_history)

    for news in recent_news:
        if news["doc_id"] not in user_history_news_id_set:
            final_news.append(news)

    return final_news


def generate_user_recommender_list(user_info):
    if user_info is None:
        # 自动更新所有用户的推荐列表
        all_users = User.objects()
        for user in all_users:
            update_user_recommender_list(str(user.id))
    else:
        update_user_recommender_list(user_info["userId"])

    # 将推荐列表存入数据库中
    user_recommender_list = UserRecommenderList()
    user_recommender_list["user_id"] = int(user_info["userId"])
    user_recommender_list["recommender_list"] = candidate_news_id_list
    user_recommender_list.save()


def similar_news_list(doc_id):
    key = doc_id + "_similar"
    if rd.exists(key) > 0:
        print("存在类似")
        similar_news_list = json.loads(rd.get(key))
    else:
        print("不存在类似")
        news_utils: NewsUtils = get_val(NEWS_UTILS)
        similar_news_list = news_utils.cal_similar_news(doc_id)
        rd.set(key, json.dumps(similar_news_list), ex=config["DBConfig"]["redis"]["expires"])
    res = []
    idx = random.sample(range(0, 29), 5)
    for index in idx:
        res.append(similar_news_list[index])
    return res
