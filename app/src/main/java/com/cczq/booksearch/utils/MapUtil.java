package com.cczq.booksearch.utils;

/**
 * Created by bb on 2016/12/9.
 */

import android.app.Activity;
import android.graphics.Color;
import android.widget.Toast;

import com.fengmap.android.analysis.navi.FMNaviAnalyser;
import com.fengmap.android.analysis.navi.FMNaviResult;
import com.fengmap.android.map.FMMap;
import com.fengmap.android.map.geometry.FMMapCoord;
import com.fengmap.android.map.layer.FMImageLayer;
import com.fengmap.android.map.layer.FMLineLayer;
import com.fengmap.android.map.marker.FMImageMarker;
import com.fengmap.android.map.marker.FMLineMarker;
import com.fengmap.android.map.marker.FMSegment;
import com.fengmap.android.map.style.FMImageMarkerStyle;
import com.fengmap.android.map.style.FMLineMarkerStyle;

import java.util.ArrayList;

public class MapUtil {

    // static int choice;
    public static FMMapCoord coord = null;

    /**
     * 在指定位置添加标注物到指定的图层。
     *
     * @param layer
     *            图层
     * @param point
     *            地图坐标
     * @return 标注物
     */
    public static FMImageMarker addMarker(FMImageLayer layer, FMMapCoord point,
                                          FMImageMarkerStyle style, FMMap map) {
        // 创建标注物
        FMImageMarker marker = new FMImageMarker(point);
        marker.setStyle(style);
        // 指针对象
        long handle = layer.addMarker(marker);
        map.updateMap();
        if (handle <= 0)
            return null;
        return marker;
    }

    /**
     * 路线规划，画线
     *
     * @param startGroupId
     *            起始图层Id
     * @param startPt
     *            起始点坐标
     * @param endGroupId
     *            终点图层Id
     * @param endPt
     *            终点坐标
     * @param naviAnalyser
     *            导航分析器
     * @param lineLayer
     *            线图层
     * @param map
     *            操作地图对象
     * @param activity
     *            活动需要错误提示
     *
     * @return
     */
    public static void anlyzeNavi(int startGroupId, FMMapCoord startPt,
                                  int endGroupId, FMMapCoord endPt, FMNaviAnalyser naviAnalyser,
                                  FMLineLayer lineLayer, FMMap map, Activity activity) {

        int type = naviAnalyser.analyzeNavi(startGroupId, startPt, endGroupId,
                endPt, FMNaviAnalyser.FMNaviModule.MODULE_SHORTEST);
        if (type == FMNaviAnalyser.FMRouteCalcuResult.ROUTE_SUCCESS) {
            ArrayList<FMNaviResult> results = naviAnalyser.getNaviResults();
            // 画线
            FMLineMarkerStyle lineStyle1 = new FMLineMarkerStyle();
            lineStyle1.setFillColor(Color.RED);
            lineStyle1.setLineWidth(0.5f);
            lineStyle1.setLineMode(FMLineMarker.LineMode.FMLINE_CIRCLE);
            lineStyle1.setLineType(FMLineMarker.LineType.FMLINE_DASHED);

            FMLineMarker line = new FMLineMarker();
            line.setStyle(lineStyle1);

            for (FMNaviResult r : results) {
                FMSegment s = new FMSegment(r.getGroupId(), r.getPointList());
                line.addSegment(s);
            }
            lineLayer.addMarker(line); // 设置线

            results.clear();

            map.updateMap();
        } else if (type == FMNaviAnalyser.FMRouteCalcuResult.ROUTE_FAILURE_NO_STAIR_FLOORS) {
            Toast.makeText(activity, "没有电梯或者扶梯进行跨层导航", Toast.LENGTH_SHORT)
                    .show();
        } else if (type == FMNaviAnalyser.FMRouteCalcuResult.ROUTE_FAILURE_NOTSUPPORT_FLOORS) {
            Toast.makeText(activity, "不支持跨层导航", Toast.LENGTH_SHORT).show();
        } else if (type == FMNaviAnalyser.FMRouteCalcuResult.ROUTE_PARAM_ERROR) {
            Toast.makeText(activity, "导航参数出错", Toast.LENGTH_SHORT).show();
        } else if (type == FMNaviAnalyser.FMRouteCalcuResult.ROUTE_FAILURE_TOO_CLOSE) {
            Toast.makeText(activity, "太近了", Toast.LENGTH_SHORT).show();
        } else if (type == FMNaviAnalyser.FMRouteCalcuResult.ROUTE_DATABASE_ERROR) {
            Toast.makeText(activity, "数据库出错", Toast.LENGTH_SHORT).show();
        } else if (type == FMNaviAnalyser.FMRouteCalcuResult.ROUTE_FAILURE_NO_END) {
            Toast.makeText(activity, "数据出错，终点不在其对应层的数据中", Toast.LENGTH_SHORT)
                    .show();
        } else if (type == FMNaviAnalyser.FMRouteCalcuResult.ROUTE_FAILURE_NO_START) {
            Toast.makeText(activity, "数据错误，起点不在其对应层的数据中", Toast.LENGTH_SHORT)
                    .show();
        } else if (type == FMNaviAnalyser.FMRouteCalcuResult.ROUTE_FAILURE_NO_FMDBKERNEL) {
            Toast.makeText(activity, "底层指针错误", Toast.LENGTH_SHORT).show();
        }
    }
}
