package com.kingyon.elevator.uis.adapters.adaptertwo;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.blankj.utilcode.util.KeyboardUtils;
import com.blankj.utilcode.util.LogUtils;
import com.kingyon.elevator.R;
import com.kingyon.elevator.data.DataSharedPreferences;
import com.kingyon.elevator.entities.CommentEntity;
import com.kingyon.elevator.entities.entities.CommentListEntity;
import com.kingyon.elevator.entities.entities.ConentEntity;
import com.kingyon.elevator.uis.actiivty2.content.ContentDetailsActivity;
import com.kingyon.elevator.uis.activities.inputcomment.EditorCallback;
import com.kingyon.elevator.uis.activities.inputcomment.InputCommentActivity;
import com.kingyon.elevator.uis.dialogs.DeleteShareDialog;
import com.kingyon.elevator.uis.dialogs.ReportShareDialog;
import com.kingyon.elevator.utils.utilstwo.ConentUtils;
import com.kingyon.elevator.utils.utilstwo.TokenUtils;
import com.leo.afbaselibrary.uis.activities.BaseActivity;
import com.leo.afbaselibrary.utils.GlideUtils;
import com.leo.afbaselibrary.utils.TimeUtil;

import java.util.List;

import static com.czh.myversiontwo.utils.CodeType.CANCEL_LIKE;
import static com.czh.myversiontwo.utils.CodeType.HOME_COMMENT;
import static com.czh.myversiontwo.utils.CodeType.HOME_CONTENT;
import static com.czh.myversiontwo.utils.CodeType.LIKE;
import static com.czh.myversiontwo.utils.Constance.ACTIVITY_COMMENT_TWO;

/**
 * Created By Admin  on 2020/4/16
 * Email : 163235610@qq.com
 * Author:Mrczh
 * Instructions:
 */
public class ContentCommentsAdapter extends RecyclerView.Adapter<ContentCommentsAdapter.ViewHolder> {

    BaseActivity context;
    List<CommentListEntity> conentEntity;
    String type;
    GetRefresh getRefresh;
    /**
     * type 1 一级 2 子级
     * */
    public ContentCommentsAdapter(BaseActivity context,String type, GetRefresh getRefresh){
        this.context = context;
        this.type = type;
        this.getRefresh = getRefresh;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_content_comments,parent,false);

        return new ContentCommentsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CommentListEntity commentListEntity = conentEntity.get(position);
        if (holder!=null){
            holder.tv_comment.setText(commentListEntity.comment);
            if (type.equals("1")){
                holder.tv_comment_hf.setText(commentListEntity.child.size()+"条回复");
            }else {
                holder.tv_comment_hf.setText("回复");
            }
            holder.tv_like_number.setText(commentListEntity.likesNum+"");
            holder.tv_name.setText(commentListEntity.nickname);
            holder.tv_time.setText(TimeUtil.getRecentlyTime(commentListEntity.createTime));
            GlideUtils.loadRoundImage(context, commentListEntity.photo, holder.img_portrait,20);
//            if (conentEntity.get(position).liked){
//                holder.img_like.setImageResource(R.mipmap.ic_small_like);
//            }else {
//                holder.img_like.setImageResource(R.mipmap.ic_small_like_off);
//            }
            holder.tv_comment_hf.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (type.equals("1")) {
                        if (commentListEntity.child.size() > 0) {
                            /*跳转*/
                            ARouter.getInstance().build(ACTIVITY_COMMENT_TWO)
                                    .withInt("contentId", commentListEntity.contentId)
                                    .withInt("onId", commentListEntity.id)
                                    .navigation();
                        } else {
                            InputCommentActivity.openEditor(context, new EditorCallback() {
                                @Override
                                public void onCancel() {
                                    LogUtils.d("关闭输入法-------------");
                                    KeyboardUtils.hideSoftInput(context);
                                }

                                @Override
                                public void onSubmit(String content) {
                                    ConentUtils.httpComment(context, commentListEntity.contentId,
                                            commentListEntity.id, content, new ConentUtils.IsSuccedListener() {
                                                @Override
                                                public void onisSucced(boolean isSucced) {
                                                    getRefresh.onRefresh(isSucced);
                                                }
                                            });
                                }

                                @Override
                                public void onAttached(ViewGroup rootView) {

                                }

                                @Override
                                public void onIcon() {

                                }
                            });
                        }
                    }else {
                        InputCommentActivity.openEditor(context, new EditorCallback() {
                            @Override
                            public void onCancel() {
                                LogUtils.d("关闭输入法-------------");
                                KeyboardUtils.hideSoftInput(context);
                            }
                            @Override
                            public void onSubmit(String content) {
                                ConentUtils.httpComment(context, commentListEntity.contentId,
                                        commentListEntity.id, content, new ConentUtils.IsSuccedListener() {
                                            @Override
                                            public void onisSucced(boolean isSucced) {
                                                    getRefresh.onRefresh(isSucced);
                                            }
                                        });
                            }

                            @Override
                            public void onAttached(ViewGroup rootView) {
                            }

                            @Override
                            public void onIcon() {

                            }
                        });
                    }
                }
            });

            holder.img_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (TokenUtils.isToken(context)){
                        LogUtils.e(DataSharedPreferences.getCreatateAccount(),commentListEntity.createAccount);
                        if (TokenUtils.isCreateAccount(commentListEntity.createAccount)){
                            /*删除*/
                            DeleteShareDialog deleteShareDialog = new DeleteShareDialog(context,commentListEntity.id,
                                    ContentCommentsAdapter.this,"3",position,null,conentEntity);
                            deleteShareDialog.show();
                        }else {
                            /*举报*/
                            ReportShareDialog reportShareDialog = new ReportShareDialog(context,commentListEntity.id,HOME_COMMENT);
                            reportShareDialog.show();
                        }
                    }

                }
            });
            holder.img_like.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (true) {
                        holder.img_like.setImageResource(R.mipmap.ic_small_like);
                    }else {
                        holder.img_like.setImageResource(R.mipmap.ic_small_like_off);
                    }

                }
            });

        }
    }

    @Override
    public int getItemCount() {
        return conentEntity.size();
    }

    public void addData(List<CommentListEntity> listEntities) {
        this.conentEntity = listEntities;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_comment_hf,tv_time,tv_comment,tv_like_number,tv_name;
        ImageView img_like,img_portrait,img_delete;
        public ViewHolder(View itemView) {
            super(itemView);
            tv_comment = itemView.findViewById(R.id.tv_comment);
            tv_comment_hf = itemView.findViewById(R.id.tv_comment_hf);
            tv_time = itemView.findViewById(R.id.tv_time);
            tv_like_number = itemView.findViewById(R.id.tv_like_number);
            tv_name = itemView.findViewById(R.id.tv_name);
            img_like = itemView.findViewById(R.id.img_like);
            img_portrait = itemView.findViewById(R.id.img_portrait);
            img_delete = itemView.findViewById(R.id.img_delete);
        }
    }

    public interface GetRefresh{
        void onRefresh(boolean isSucced) ;
    }
}
