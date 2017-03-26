package and.com.moviemanic.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

import and.com.moviemanic.R;
import and.com.moviemanic.model.Trailer;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by dell on 10-03-2017.
 */

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.MyViewHolder> {

    private List<Trailer> trailerList;
    private Context context;

    public TrailerAdapter(List<Trailer> trailerList, Context context) {
        this.trailerList = trailerList;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.trailer_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Trailer trailer = trailerList.get(position);
        Picasso.with(context).load("http://img.youtube.com/vi/" + trailer.getKey().trim() + "/0.jpg").into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return trailerList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.trailer_image)
        ImageView imageView;
        public MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
}
