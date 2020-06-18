package com.mtc.mindbook.models.playlist;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.mtc.mindbook.R;
import com.mtc.mindbook.models.responseObj.DefaultResponseObj;
import com.mtc.mindbook.models.responseObj.playlist.PlaylistDataResponseObj;
import com.mtc.mindbook.remote.APIService;
import com.mtc.mindbook.remote.APIUtils;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;

public class AddlistAdapter extends RecyclerView.Adapter<AddlistAdapter.RecyclerViewHolder> {
    private List<PlaylistDataResponseObj> data = new ArrayList<>();
    private String bookId;
    private Dialog baseDialog;
    private Context context = null;

    public AddlistAdapter(List<PlaylistDataResponseObj> data, Context context, String bookId, Dialog baseDialog) {
        this.data = data;
        this.bookId = bookId;
        this.baseDialog = baseDialog;
        this.context = context;
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.addlist_item, parent, false);
        return new RecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AddlistAdapter.RecyclerViewHolder holder, final int position) {
        holder.playlistName.setText(data.get(position).getName());
        holder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                APIService apiService = APIUtils.getUserService();
                SharedPreferences sharedPrefs = v.getContext().getSharedPreferences("userDataPrefs", MODE_PRIVATE);
                String token = "Bearer " + sharedPrefs.getString("accessToken", "");
                Call<DefaultResponseObj> addBook = apiService.addBookToPlaylist(token, data.get(position).getPlaylistId(), bookId);
                addBook.enqueue(new Callback<DefaultResponseObj>() {
                    @Override
                    public void onResponse(Call<DefaultResponseObj> call, Response<DefaultResponseObj> response) {
                        if (response.body() == null) {
                            onFailure(call, null);
                            return;
                        }
                        if (response.body().getMesssage().equals("Success")) {
                            Toast.makeText(v.getContext(), context.getResources().getString(R.string.toast_add_book) + " " + data.get(position).getName(), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(v.getContext(), context.getResources().getString(R.string.toast_book_already_in_playlist) + " " + data.get(position).getName(), Toast.LENGTH_SHORT).show();
                        }
                        baseDialog.dismiss();
                    }

                    @Override
                    public void onFailure(Call<DefaultResponseObj> call, Throwable t) {
                        Log.d("failed", "onFailure: " + t.getMessage());
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder {
        TextView playlistName;
        TextView bookCount;
        LinearLayout container;

        public RecyclerViewHolder(View itemView) {
            super(itemView);
            playlistName = itemView.findViewById(R.id.addlist_name);
            container = itemView.findViewById(R.id.addlist_container);
        }
    }
}

