/*
 * Copyright (C) 2016 Angad Singh
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.angads25.filepicker.controller.adapters;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.github.angads25.filepicker.R;
import com.github.angads25.filepicker.controller.NotifyItemChecked;
import com.github.angads25.filepicker.model.DialogConfigs;
import com.github.angads25.filepicker.model.DialogProperties;
import com.github.angads25.filepicker.model.FileListItem;
import com.github.angads25.filepicker.model.MarkedItemList;
import com.github.angads25.filepicker.widget.MaterialCheckbox;
import com.github.angads25.filepicker.widget.OnCheckedChangeListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

/* <p>
 * Created by Angad Singh on 09-07-2016.
 * </p>
 */

/**
 * Adapter Class that extends {@link BaseAdapter} that is
 * used to populate {@link ListView} with file info.
 */
public class FileListAdapter extends RecyclerView.Adapter<FileListAdapter.ViewHolder> {
    private ArrayList<FileListItem> listItem;
    private Context context;
    private DialogProperties properties;
    private NotifyItemChecked notifyItemChecked;

    public FileListAdapter(ArrayList<FileListItem> listItem, Context context, DialogProperties properties) {
        this.listItem = listItem;
        this.context = context;
        this.properties = properties;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_file_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final FileListItem item = listItem.get(position);
        if (MarkedItemList.hasItem(item.getLocation())) {
            Animation animation = AnimationUtils.loadAnimation(context, R.anim.marked_item_animation);
            holder.rootView.setAnimation(animation);
        } else {
            Animation animation = AnimationUtils.loadAnimation(context, R.anim.unmarked_item_animation);
            holder.rootView.setAnimation(animation);
        }
        if (item.isDirectory()) {
            holder.type_icon.setImageResource(R.mipmap.ic_type_folder);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                holder.type_icon.setColorFilter(context.getResources().getColor(R.color.colorPrimary, context.getTheme()));
            } else {
                holder.type_icon.setColorFilter(context.getResources().getColor(R.color.colorPrimary));
            }
            if (properties.selection_type == DialogConfigs.FILE_SELECT) {
                holder.fmark.setVisibility(View.INVISIBLE);
            } else {
                holder.fmark.setVisibility(View.VISIBLE);
            }
        } else {
            holder.type_icon.setImageResource(R.mipmap.ic_type_file);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                holder.type_icon.setColorFilter(context.getResources().getColor(R.color.colorAccent, context.getTheme()));
            } else {
                holder.type_icon.setColorFilter(context.getResources().getColor(R.color.colorAccent));
            }
            if (properties.selection_type == DialogConfigs.DIR_SELECT) {
                holder.fmark.setVisibility(View.INVISIBLE);
            } else {
                holder.fmark.setVisibility(View.VISIBLE);
            }
        }
        holder.type_icon.setContentDescription(item.getFilename());
        holder.name.setText(item.getFilename());
        SimpleDateFormat sdate = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        SimpleDateFormat stime = new SimpleDateFormat("hh:mm aa", Locale.getDefault());
        Date date = new Date(item.getTime());
        if (position == 0 && item.getFilename().startsWith(context.getString(R.string.label_parent_dir))) {
            holder.type.setText(R.string.label_parent_directory);
        } else {
            holder.type.setText(context.getString(R.string.last_edit) + sdate.format(date) + ", " + stime.format(date));
        }
        if (holder.fmark.getVisibility() == View.VISIBLE) {
            if (position == 0 && item.getFilename().startsWith(context.getString(R.string.label_parent_dir))) {
                holder.fmark.setVisibility(View.INVISIBLE);
            }
            if (MarkedItemList.hasItem(item.getLocation())) {
                holder.fmark.setChecked(true);
            } else {
                holder.fmark.setChecked(false);
            }
        }

        holder.fmark.setOnCheckedChangedListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(MaterialCheckbox checkbox, boolean isChecked) {
                item.setMarked(isChecked);
                if (item.isMarked()) {
                    if (properties.selection_mode == DialogConfigs.MULTI_MODE) {
                        MarkedItemList.addSelectedItem(item);
                    } else {
                        MarkedItemList.addSingleFile(item);
                    }
                } else {
                    MarkedItemList.removeSelectedItem(item.getLocation());
                }
                notifyItemChecked.notifyCheckBoxIsClicked();
            }
        });
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return listItem.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        AppCompatImageView type_icon;
        AppCompatTextView name, type;
        MaterialCheckbox fmark;

        View rootView;

        ViewHolder(View itemView) {
            super(itemView);
            rootView = itemView;
            name = itemView.findViewById(R.id.fname);
            type = itemView.findViewById(R.id.ftype);
            type_icon = itemView.findViewById(R.id.image_type);
            fmark = itemView.findViewById(R.id.file_mark);
        }
    }

    public void setNotifyItemCheckedListener(NotifyItemChecked notifyItemChecked) {
        this.notifyItemChecked = notifyItemChecked;
    }
}
