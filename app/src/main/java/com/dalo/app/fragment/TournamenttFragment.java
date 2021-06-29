package com.dalo.app.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.google.android.material.snackbar.Snackbar;
import com.dalo.app.Constant;
import com.dalo.app.R;
import com.dalo.app.activity.ContestActivity;

import com.dalo.app.activity.RewardActivity;
import com.dalo.app.activity.TournamentPlay;
import com.dalo.app.helper.ApiConfig;
import com.dalo.app.helper.AppController;
import com.dalo.app.helper.Session;
import com.dalo.app.helper.UserSessionManager;
import com.dalo.app.helper.Utils;
import com.dalo.app.model.Model;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class TournamenttFragment extends Fragment {

    RecyclerView recyclerView;
    TextView nodata;
    ProgressBar progressbar;
    UserSessionManager session;
    private List<Model> historyList = new ArrayList<>();
    QuizListAdapter adapter;
    String quiztype;
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();
    String type;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_quiz_list, container, false);
        type = getArguments().getString("current_page");
        session = new UserSessionManager(getActivity());
        progressbar = v.findViewById(R.id.progressbar);
        recyclerView = v.findViewById(R.id.recycleview);
        nodata = v.findViewById(R.id.nodata);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        prepareData(type);
        return v;
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void prepareData(final String type) {
        if (isNetworkAvailable()) {
            progressbar.setVisibility(View.VISIBLE);
            Map<String, String> params = new HashMap<>();
            params.put(Constant.GETCONTEST, Constant.GETDATAKEY);
            params.put(Constant.userId, Session.getUserData(Session.USER_ID, getActivity()));
            ApiConfig.RequestToVolley(new ApiConfig.VolleyCallback() {
                @Override
                public void onSuccess(boolean result, String response) {
                    if (result) {
                        try {
                            JSONObject jsonObject1 = new JSONObject(response);
                            //System.out.println("Values::==" + response);
                            JSONObject jsonObject = null;
                            if (type.equalsIgnoreCase(ContestActivity.tab2))
                                jsonObject = jsonObject1.getJSONObject(Constant.LIVE_CONTEST);
                            else if (type.equalsIgnoreCase(ContestActivity.tab3))
                                jsonObject = jsonObject1.getJSONObject(Constant.UPCOMING_CONTEST);
                            else if (type.equalsIgnoreCase(ContestActivity.tab1))
                                jsonObject = jsonObject1.getJSONObject(Constant.PAST_CONTEST);
                            if (!jsonObject.getBoolean("error")) {
                                historyList.clear();
                                JSONArray object = jsonObject.getJSONArray("data");
                                for (int i = 0; i < object.length(); i++) {
                                    JSONObject jsonobj = object.getJSONObject(i);
                                    Model model = new Model(jsonobj.getString(Constant.ID), jsonobj.getString(Constant.name), jsonobj.getString(Constant.STARTDATE), jsonobj.getString(Constant.ENDDATE), jsonobj.getString(Constant.DESCRIPTION), jsonobj.getString(Constant.IMAGE), jsonobj.getString(Constant.ENTRY), jsonobj.getString(Constant.TOPUSERS), jsonobj.getString(Constant.POINTS), jsonobj.getString(Constant.DATECREATED), jsonobj.getString(Constant.PARTICIPANTS), "");
                                    historyList.add(model);
                                    progressbar.setVisibility(View.GONE);
                                }
                                adapter = new QuizListAdapter(historyList);
                                recyclerView.setVisibility(View.VISIBLE);
                                recyclerView.setAdapter(adapter);

                            } else {
                                recyclerView.setVisibility(View.GONE);
                                nodata.setText(jsonObject.getString("message"));
                                nodata.setVisibility(View.VISIBLE);
                                progressbar.setVisibility(View.GONE);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }, params);
        } else {
            Snackbar snackbar = Snackbar
                    .make(getActivity().findViewById(android.R.id.content), "No Network connection..!!", Snackbar.LENGTH_INDEFINITE)
                    .setActionTextColor(Color.YELLOW)
                    .setAction("RETRY", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = getActivity().getIntent();
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        }
                    });
            snackbar.show();
        }
    }

    public void reWardsNotLoad() {
        final android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(getActivity());
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View dialogView = inflater.inflate(R.layout.lifeline_dialog, null);
        dialog.setView(dialogView);
        TextView ok = (TextView) dialogView.findViewById(R.id.ok);
        TextView title = (TextView) dialogView.findViewById(R.id.title);
        TextView message = (TextView) dialogView.findViewById(R.id.message);
        message.setText(getResources().getString(R.string.not_enough_entry_coin));
        title.setText(getResources().getString(R.string.not_enough_coin));
        final android.app.AlertDialog alertDialog = dialog.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.show();
        alertDialog.setCancelable(false);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                alertDialog.dismiss();

            }

        });
    }

    public class QuizListAdapter extends RecyclerView.Adapter<QuizListAdapter.ViewHolder> {

        public List<Model> historyList;

        public QuizListAdapter(List<Model> historyList) {
            this.historyList = historyList;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.lyt_contestpage, parent, false);
            return new ViewHolder(view);
        }

        @NonNull

        @Override
        public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
            final Model model = historyList.get(position);
            holder.txtname.setText(model.getName());
            holder.txtdesc.setText(model.getDescription());

            holder.txtentry.setText(model.getEntry() + " coins");
            holder.txtparticipatns.setText(model.getParticipants());
            //System.out.println("Values ID::=" + model.getId());
            holder.imgquiz.setDefaultImageResId(R.drawable.ic_contestplaceholder);
            holder.imgquiz.setErrorImageResId(R.drawable.ic_contestplaceholder);

            if (!model.getImage().equalsIgnoreCase(""))
                holder.imgquiz.setImageUrl(model.getImage(), imageLoader);

            if (type.equalsIgnoreCase(ContestActivity.tab3)) {
                holder.btnplay.setVisibility(View.INVISIBLE);
                holder.lytparticipants.setVisibility(View.INVISIBLE);
                holder.txtdateheader.setText(getResources().getString(R.string.start_on));
                holder.txtendDate.setText(model.getStart_date());
            } else if (type.equalsIgnoreCase(ContestActivity.tab2)) {
                holder.txtdateheader.setText(getResources().getString(R.string.ends_on));
                holder.txtendDate.setText(model.getEnd_date());
            } else if (type.equalsIgnoreCase(ContestActivity.tab1)) {
                holder.txtdateheader.setText(getResources().getString(R.string.ending_on));
                holder.txtendDate.setText(model.getEnd_date());
                holder.btnplay.setText(getResources().getString(R.string.leaderboard));
            }

            holder.lytmain.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (holder.txtdesc.getVisibility() == View.VISIBLE) {
                        holder.btnarrow.setImageDrawable(getResources().getDrawable(R.drawable.ic_arrow_expand));
                        holder.txtdesc.setVisibility(View.GONE);
                    } else {
                        holder.btnarrow.setImageDrawable(getResources().getDrawable(R.drawable.ic_arrow_collapse));
                        holder.txtdesc.setVisibility(View.VISIBLE);
                    }
                }
            });

            holder.btnplay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (type.equalsIgnoreCase(ContestActivity.tab1)) {
                        Intent i = new Intent(getActivity(), RewardActivity.class);
                        i.putExtra("data", model.getId());
                        startActivity(i);
                    } else {
                        if (Constant.TOTAL_COINS < Double.parseDouble(model.getEntry())) {
                            reWardsNotLoad();
                            // Toast.makeText(getActivity(), getResources().getString(R.string.not_enough_entry_coin), Toast.LENGTH_SHORT).show();
                        } else {
                            Constant.TOTAL_COINS = Constant.TOTAL_COINS - Integer.parseInt(model.getEntry());
                            Utils.UpdateCoin(getActivity(), "-" + model.getEntry());
                            Intent i = new Intent(getActivity(), TournamentPlay.class);
                            i.putExtra("id", model.getId());
                            i.putExtra("entrypoint", model.getEntry());
                            i.putExtra("title", model.getName());
                            i.putExtra("from", "contest");
                            startActivity(i);
                            getActivity().finish();
                        }
                    }
                }
            });

            holder.imgInfo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showPriceDialog(getActivity(), model.getPoints());
                }
            });
        }

        @Override
        public int getItemCount() {
            return historyList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            public TextView txtname, txtdesc, btnplay, txtendDate, txtentry, txtdateheader, txtparticipatns;
            NetworkImageView imgquiz;
            LinearLayout lytparticipants, lytmain;
            ImageView btnarrow, imgInfo;


            public ViewHolder(View itemView) {
                super(itemView);

                btnplay = (TextView) itemView.findViewById(R.id.btnplay);
                btnplay.setVisibility(View.VISIBLE);
                lytmain = (LinearLayout) itemView.findViewById(R.id.lytmain);
                btnarrow = (ImageView) itemView.findViewById(R.id.btnarrow);
                imgInfo = (ImageView) itemView.findViewById(R.id.imgInfo);
                txtname = (TextView) itemView.findViewById(R.id.txttitle);
                txtdesc = (TextView) itemView.findViewById(R.id.txtdesc);
                txtendDate = (TextView) itemView.findViewById(R.id.txtendDate);
                txtentry = (TextView) itemView.findViewById(R.id.txtentry);
                imgquiz = (NetworkImageView) itemView.findViewById(R.id.imgquiz);
                txtdateheader = (TextView) itemView.findViewById(R.id.txtdateheader);
                txtparticipatns = (TextView) itemView.findViewById(R.id.txtparticipatns);
                lytparticipants = (LinearLayout) itemView.findViewById(R.id.lytparticipats);
            }

        }
    }

    public void showPriceDialog(Activity activity, String priceArray) {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(activity);
        LayoutInflater inflater1 = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View dialogView = inflater1.inflate(R.layout.lyt_price_dialog, null);
        dialog.setView(dialogView);
        RecyclerView recyclerView = dialogView.findViewById(R.id.recyclerView);
        ImageView imgClose = dialogView.findViewById(R.id.imgClose);
        recyclerView.setLayoutManager(new LinearLayoutManager(activity));
        AlertDialog alertDialog = dialog.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        try {
            JSONArray jsonArray = new JSONArray(priceArray);
            ArrayList<Model> priceList = new ArrayList<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                priceList.add(new Model(jsonObject.getString(Constant.TOP_WINNERS), jsonObject.getString(Constant.POINTS)));
            }

            PriceAdapter priceAdapter = new PriceAdapter(activity, priceList);
            recyclerView.setAdapter(priceAdapter);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }

    public static class PriceAdapter extends RecyclerView.Adapter<PriceAdapter.ItemRowHolder> {
        private ArrayList<Model> dataList;
        private Context mContext;

        public PriceAdapter(Context context, ArrayList<Model> dataList) {
            this.dataList = dataList;
            this.mContext = context;

        }

        @Override
        public ItemRowHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.lyt_price, parent, false);
            return new ItemRowHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull ItemRowHolder holder, final int position) {

            final Model model = dataList.get(position);
            holder.tvPrice.setText("Price : " + model.getPoints() + " Coins");
            holder.tvWinner.setText("Top User: " + model.getTop_users());

        }

        @Override
        public int getItemCount() {
            return (dataList.size());
        }

        public class ItemRowHolder extends RecyclerView.ViewHolder {

            public TextView tvWinner, tvPrice;


            public ItemRowHolder(View itemView) {
                super(itemView);

                tvWinner = itemView.findViewById(R.id.tvWinner);
                tvPrice = itemView.findViewById(R.id.tvPrice);
            }

        }

    }

    @Override
    public void onResume() {
        super.onResume();

    }
}
