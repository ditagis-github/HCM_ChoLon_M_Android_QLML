package hcm.ditagis.com.tanhoa.qlts.tools;

import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ListView;

import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.data.QueryParameters;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import hcm.ditagis.com.tanhoa.qlts.QuanLySuCo;
import hcm.ditagis.com.tanhoa.qlts.R;
import hcm.ditagis.com.tanhoa.qlts.adapter.ThongKeAdapter;
import hcm.ditagis.com.tanhoa.qlts.libs.FeatureLayerDTG;

/**
 * Created by NGUYEN HONG on 6/15/2018.
 */

public class ThongKe {
    private QuanLySuCo mainActivity;
    private List<FeatureLayerDTG> mFeatureLayerDTGS;
    private List<ThongKeAdapter.Item> items;
    private ThongKeAdapter thongKeAdapter;
    private AlertDialog selectTimeDialog;

    public ThongKe() {
    }

    public ThongKe(QuanLySuCo mainActivity, List<FeatureLayerDTG> mFeatureLayerDTGS) {
        this.mainActivity = mainActivity;
        this.mFeatureLayerDTGS = mFeatureLayerDTGS;
        setup();
    }

    public QuanLySuCo getMainActivity() {
        return mainActivity;
    }

    public void setMainActivity(QuanLySuCo mainActivity) {
        this.mainActivity = mainActivity;
    }

    public List<FeatureLayerDTG> getmFeatureLayerDTGS() {
        return mFeatureLayerDTGS;
    }

    public void setmFeatureLayerDTGS(List<FeatureLayerDTG> mFeatureLayerDTGS) {
        this.mFeatureLayerDTGS = mFeatureLayerDTGS;
    }
    public void setup(){
        items = new ArrayList<>();
        thongKeAdapter = new ThongKeAdapter(mainActivity,items);
        AlertDialog.Builder builder = new AlertDialog.Builder(mainActivity, android.R.style.Theme_Holo_Light_NoActionBar_Fullscreen);
        View layout = mainActivity.getLayoutInflater().inflate(R.layout.layout_title_listview, null);
        ImageView imageView = (ImageView)layout.findViewById(R.id.img_refress);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refress();
            }
        });
        ListView listView = layout.findViewById(R.id.listview);
        listView.setAdapter(thongKeAdapter);
        builder.setView(layout);
        selectTimeDialog = builder.create();
        selectTimeDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        refress();
    }
    public void start(){
        selectTimeDialog.show();
    }
    public void refress(){
        QueryParameters queryParameters = new QueryParameters();
        String queryClause = "1=1";
        queryParameters.setWhereClause(queryClause);
        for(final FeatureLayerDTG featureLayerDTG:mFeatureLayerDTGS) {
            if(featureLayerDTG.getAction() != null && featureLayerDTG.getAction().getStatistics()) {
                final ListenableFuture<Long> longListenableFuture = featureLayerDTG.getFeatureLayer().getFeatureTable().queryFeatureCountAsync(queryParameters);
                longListenableFuture.addDoneListener(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Long aLong = longListenableFuture.get();
                            ThongKeAdapter.Item item = new ThongKeAdapter.Item(featureLayerDTG.getTitleLayer(), aLong);
                            items.add(item);
                            thongKeAdapter.notifyDataSetChanged();

                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }
    }
}