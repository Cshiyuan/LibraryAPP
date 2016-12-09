package com.cczq.booksearch.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.cczq.booksearch.BookSearchListActivity;
import com.cczq.booksearch.MainActivity;
import com.cczq.booksearch.R;
import com.cczq.booksearch.codescan.MipcaActivityCapture;
import com.cczq.booksearch.utils.MapUtil;
import com.cczq.booksearch.utils.ResourcesUtils;
import com.fengmap.android.FMErrorMsg;
import com.fengmap.android.analysis.navi.FMNaviAnalyser;
import com.fengmap.android.analysis.search.FMSearchAnalyser;
import com.fengmap.android.analysis.search.FMSearchRequest;
import com.fengmap.android.analysis.search.FMSearchResult;
import com.fengmap.android.analysis.search.model.FMSearchModelByKeywordRequest;
import com.fengmap.android.data.FMDataManager;
import com.fengmap.android.map.FMGroupInfo;
import com.fengmap.android.map.FMMap;
import com.fengmap.android.map.FMMapInfo;
import com.fengmap.android.map.FMMapView;
import com.fengmap.android.map.event.OnFMMapInitListener;
import com.fengmap.android.map.geometry.FMMapCoord;
import com.fengmap.android.map.layer.FMImageLayer;
import com.fengmap.android.map.layer.FMLineLayer;
import com.fengmap.android.map.marker.FMModel;
import com.fengmap.android.map.style.FMImageMarkerStyle;
import com.fengmap.android.utils.FMLog;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;

import java.util.ArrayList;

/**
 * Created by Shyuan on 2016/10/11.
 */

public class BookFragment extends Fragment implements OnFMMapInitListener {


    private static final long RIPPLE_DURATION = 250;

    FloatingActionsMenu menuMultipleActions;
    FloatingActionButton search_button;
    FloatingActionButton point_button;

    private final static int SCANNIN_GREQUEST_CODE = 1;
    private final static int BOOKLIST_GREQUEST_CODE = 2;


    FMMapView mapView; // 地图容器对象
    FMMap map; // 操作地图的对象
    FMMapInfo scene; // 整个场景的配置信息
    FMSearchAnalyser searchAnalyser; // 搜索分析器
    FMImageLayer mlForNaviStart; // 标致物图层，起点
    FMImageLayer mlForNaviEnd; // 标注物图层，终点
    FMLineLayer lineLayer; // 线图层
    FMNaviAnalyser naviAnalyser;
    FMMapCoord startPt, endPt; // 起点终点
    int startGroupId, endGroupId; // 起点终点所在组ID

    int floorNum; // 层数

    int focus = 0;

    int[] gids;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        map.onDestory();
    }

    @Override
    public void onResume()
    {
        super.onResume();
//        map.openMapById("00205100000590132"); //处理bug
//        refreshMap();
//        initMap();
    }

     @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_book, container, false);

        mapView = (FMMapView) view.findViewById(R.id.mapview); // 与xml建立连接IU控件
        initMap();

        menuMultipleActions = (FloatingActionsMenu) view.findViewById(R.id.multiple_actions);
        search_button = (FloatingActionButton) view.findViewById(R.id.search_button);
        search_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent List_intent = new Intent();
                // 跳转到活动ListActivity
                List_intent.setClass(getActivity(), BookSearchListActivity.class);
                getActivity().startActivityForResult(List_intent, BOOKLIST_GREQUEST_CODE);
            }
        });

        point_button = (FloatingActionButton)view.findViewById(R.id.point_button);
        point_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent Scan_intent = new Intent();
                // 跳转到活动MipcaActivityCapture
                Scan_intent.setClass(getActivity(), MipcaActivityCapture.class);
                Scan_intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                getActivity().startActivityForResult(Scan_intent, SCANNIN_GREQUEST_CODE);
            }
        });

        return view;
    }

    //初始化地图
    public void initMap()
    {
        // 拷贝地图
        String dstDir = FMDataManager.getDefaultMapDirectory() + "00205100000590132/";
        String dstFileName = "00205100000590132.fmap";
        ResourcesUtils.writeRc(getActivity(), dstDir, dstFileName, "00205100000590132.fmap");

        map = mapView.getFMMap(); // 获取地图操作对象
        map.openMapById("00205100000590132");
        map.setOnFMMapInitListener(this);
    }

    //添加标志点  i = 0表示设置起点
    public void addPoint(String point, int i) {

        int groupId = 1;

        FMSearchRequest r = new FMSearchModelByKeywordRequest(groupId, point);
        // 执行分析
        ArrayList<FMSearchResult> set = searchAnalyser // 获得搜索结果
                .executeFMSearchRequest(r);
        for (FMSearchResult info : set) { // 遍历搜索结果

            String fid = (String) info.get("fid");
            /////////
            if(i == 0)
            {

                MainActivity activity = (MainActivity)getActivity();
                if(activity.oldStartModelFid != null)
                    cleanColorModel(activity.oldStartModelFid);
                activity.oldStartModelFid = fid;
            }
            if(i == 1)
            {
                MainActivity activity = (MainActivity)getActivity();
                if(activity.oldEndModelFid != null)
                    cleanColorModel(activity.oldEndModelFid);
                activity.oldEndModelFid = fid;
            }
            ////////
            int gid = (Integer) info.get("gid");
            FMModel model = map.getFMLayerProxy().queryFMModelByFid(fid);
            FMMapCoord coord = null;
            coord = searchAnalyser.getModelCoord(groupId, gid);
            //搜索结果
            if (coord == null) {
                Toast.makeText(getActivity(), "未知错误", Toast.LENGTH_SHORT)
                        .show(); // 输出例子书架提醒

            } else if (i == 0) { // 添加起点
                if (startPt != null)
                    clear();
                startPt = coord;
                startGroupId = 1;
                // 添加起点图层
                mlForNaviStart = new FMImageLayer(map, startGroupId);
                map.addLayer(mlForNaviStart);
                // 标注物样式
                FMImageMarkerStyle style = new FMImageMarkerStyle(); // 图片名字
                style.setImageFromAssets("ico_start.png");
                // 设置标注物位于模型之上，默认就是位于地面之上
                MapUtil.addMarker(mlForNaviStart, startPt, style, map); // 起点图层，开始点坐标，样式
            } else if (i == 1) {   //添加结束点
                if (endPt != null)
                    clear();
                endPt = coord;
                endGroupId = 1;
                // 添加终点图层
                mlForNaviEnd = new FMImageLayer(map, endGroupId);
                map.addLayer(mlForNaviEnd);
                // 标注物样式
                FMImageMarkerStyle style = new FMImageMarkerStyle(); // 图片名字
                style.setImageFromAssets("ico_end.png");
                MapUtil.addMarker(mlForNaviEnd, endPt, style, map);
            }
//            model.getColor();
            MainActivity activity = (MainActivity)getActivity();
            activity.defaultColor = model.getColor();
            model.setColor(Color.rgb(255, 0, 0)); // 设置模型颜色，红色
            navi_Line();  //生成导航线
            map.updateMap();  //更新地图
        }
    }

    //生成导航线
    private void navi_Line() {
        if (startPt != null && endPt != null) {
            MapUtil.anlyzeNavi(startGroupId, startPt, endGroupId, endPt,
                    naviAnalyser, lineLayer, map, getActivity());
        }
    }

    //清除导航线
    private void clear() {
        // 清除所有线
        if (lineLayer != null) {
            lineLayer.removeAll();
        }
        // 清除标注物
        if (mlForNaviStart != null) { // 清除起点
            mlForNaviStart.removeAll();
            map.removeLayer(mlForNaviStart); // 移除图层
        }
        if (mlForNaviEnd != null) { // 清除终点
            mlForNaviEnd.removeAll();
            map.removeLayer(mlForNaviEnd); // 移除图层
        }
    }

    @Override
    public void onMapInitSuccess(String s) {

        scene = map.getFMMapInfo();
        floorNum = scene.getGroups().size(); // 场景有多少层
        ArrayList<FMGroupInfo> groups = scene.getGroups();
        floorNum = groups.size(); // 有多少层
        gids = new int[floorNum];
        for (int i = 0; i < floorNum; i++) {
            gids[i] = groups.get(i).getGroupId(); // 获得每一层的id
        }

        focus = 0;
        if (floorNum % 2 == 0) {
            focus = floorNum / 2;
        } else {
            focus = (floorNum + 1) / 2;
        }

        map.setMultiDisplay(gids, focus); // 设置多层显示，及焦点层
        map.showCompass(false); // 设置指南针

        lineLayer = map.getFMLayerProxy().getFMLineLayer(); // 线图层
        map.addLayer(lineLayer); // 将图层添加到地图
        naviAnalyser = new FMNaviAnalyser(map); // 导航分析器

        // TODO Auto-generated method stub
        searchAnalyser = new FMSearchAnalyser(map); // 初始化搜索分析器
        map.updateMap(); // 更新
//        refreshMap();
    }

    @Override
    public void onMapInitFailure(String s, int i) {

        FMLog.i("FMMap Init", FMErrorMsg.getErrorMsg(i));

    }


    //清楚fid模型的颜色
    public void cleanColorModel(String fid)
    {
        MainActivity activity = (MainActivity)getActivity();
        FMModel model = map.getFMLayerProxy().queryFMModelByFid(fid);
        model.setColor(activity.defaultColor);
    }
}
