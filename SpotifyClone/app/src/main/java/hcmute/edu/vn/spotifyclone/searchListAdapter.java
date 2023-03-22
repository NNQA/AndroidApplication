package hcmute.edu.vn.spotifyclone;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.imageview.ShapeableImageView;

import java.util.List;

public class searchListAdapter extends RecyclerView.Adapter<searchListAdapter.ViewHolder> {

    private List<user> mluser;

    public searchListAdapter(List<user> mluser) {
        this.mluser = mluser;
    }

    @NonNull
    @Override
    public searchListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_search, parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull searchListAdapter.ViewHolder holder, int position) {
        user user = mluser.get(position);
        if(user == null) return;
        holder.imageView.setImageResource(user.getImage());
        holder.txtName.setText(user.getName());
        holder.txtad.setText(user.getAddress());
    }

    @Override
    public int getItemCount() {
        if(mluser != null) return mluser.size();
        return 0;
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        ShapeableImageView imageView;
        TextView txtName, txtad;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageSearch);
            txtName = itemView.findViewById(R.id.titleName);
            txtad = itemView.findViewById(R.id.desciption);
        }
    }
}
