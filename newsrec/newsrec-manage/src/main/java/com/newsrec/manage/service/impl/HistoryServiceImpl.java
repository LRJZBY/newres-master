package com.newsrec.manage.service.impl;

import java.util.List;
import com.newsrec.common.utils.DateUtils;
import com.newsrec.manage.domain.vo.HistoryVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.newsrec.manage.mapper.HistoryMapper;
import com.newsrec.manage.domain.History;
import com.newsrec.manage.service.IHistoryService;

/**
 * 浏览历史Service业务层处理
 *
 * @author weiyan
 * @date 2025-02-20
 */
@Service
public class HistoryServiceImpl implements IHistoryService
{
    @Autowired
    private HistoryMapper historyMapper;

    /**
     * 查询浏览历史
     *
     * @param historyId 浏览历史主键
     * @return 浏览历史
     */
    @Override
    public History selectHistoryByHistoryId(Long historyId)
    {
        return historyMapper.selectHistoryByHistoryId(historyId);
    }

    /**
     * 查询浏览历史列表
     *
     * @param history 浏览历史
     * @return 浏览历史
     */
    @Override
    public List<History> selectHistoryList(History history)
    {
        return historyMapper.selectHistoryList(history);
    }

    /**
     * 新增浏览历史
     *
     * @param history 浏览历史
     * @return 结果
     */
    @Override
    public int insertHistory(History history)
    {
        history.setCreateTime(DateUtils.getNowDate());
        return historyMapper.insertHistory(history);
    }

    /**
     * 修改浏览历史
     *
     * @param history 浏览历史
     * @return 结果
     */
    @Override
    public int updateHistory(History history)
    {
        history.setUpdateTime(DateUtils.getNowDate());
        return historyMapper.updateHistory(history);
    }

    /**
     * 批量删除浏览历史
     *
     * @param historyIds 需要删除的浏览历史主键
     * @return 结果
     */
    @Override
    public int deleteHistoryByHistoryIds(Long[] historyIds)
    {
        return historyMapper.deleteHistoryByHistoryIds(historyIds);
    }

    /**
     * 删除浏览历史信息
     *
     * @param historyId 浏览历史主键
     * @return 结果
     */
    @Override
    public int deleteHistoryByHistoryId(Long historyId)
    {
        return historyMapper.deleteHistoryByHistoryId(historyId);
    }


    /**
     * 查询浏览历史列表Vo
     *
     * @param history 浏览历史
     * @return 浏览历史集合
     */
    @Override
    public List<HistoryVo> selectHistoryVoList(History history){
        return historyMapper.selectHistoryVoList(history);
    };
}
