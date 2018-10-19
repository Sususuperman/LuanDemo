package com.hywy.luanhzt.task;

import android.content.Context;

import com.cs.common.utils.StringUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hywy.luanhzt.HttpUrl;
import com.hywy.luanhzt.adapter.item.ContactItem;
import com.hywy.luanhzt.adapter.item.ContactSubItem;
import com.hywy.luanhzt.adapter.item.EvaluatedFormsExpandableItem;
import com.hywy.luanhzt.adapter.item.HzContactItem;
import com.hywy.luanhzt.adapter.item.MapClassifyHeaderItem;
import com.hywy.luanhzt.entity.Contact;
import com.hywy.luanhzt.entity.HzContact;
import com.hywy.luanhzt.key.Key;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * 河长通讯录列表task
 *
 * @author Superman
 */

public class GetHzContactListTask extends BaseRequestTask {


    public GetHzContactListTask(Context context) {
        super(context);
    }

    @Override
    public String url() {
        return HttpUrl.getUrl(HttpUrl.RMS_APP_TXL_HZLIST);
    }

    @Override
    public Object request(String json) throws Exception {
        Type type = new TypeToken<List<HzContact>>() {
        }.getType();
        List<HzContact> list = new Gson().fromJson(json, type);
        if (StringUtils.isNotNullList(list)) {
//            List<HzContactItem> items = new ArrayList<>();
//            for (HzContact contact : list) {
//                List<HzContact.DealsBean> dealsBeans = contact.getDeals();
//                if (StringUtils.isNotNullList(dealsBeans)) {
//                    for (int i = 0; i < dealsBeans.size(); i++) {
//                        HzContactItem item = null;
//                        HzContact.DealsBean cdb = dealsBeans.get(i);
//                        if (i == 0) {
//                            item = new HzContactItem(cdb, new MapClassifyHeaderItem(contact.getADNM()));
//                        } else {
//                            item = new HzContactItem(cdb);
//                        }
//                        items.add(item);
//                    }
//                }
//            }
            List<EvaluatedFormsExpandableItem> items = initForms(list);
            result.put(Key.ITEMS, items);
        }
        return list;
    }

    private List<EvaluatedFormsExpandableItem> initForms(List<HzContact> list) {
        final List<EvaluatedFormsExpandableItem> parent = new ArrayList<>();
        for (HzContact hzContact : list) {
            EvaluatedFormsExpandableItem item = newAnimatorItem(hzContact);
            if (item != null) parent.add(item);
        }
        return parent;
    }

    public EvaluatedFormsExpandableItem newAnimatorItem(HzContact hzContact) {
        if (hzContact != null) {
            EvaluatedFormsExpandableItem animatorItem = new EvaluatedFormsExpandableItem(hzContact.getADNM(), hzContact.getTYPE());
            List<HzContact.DealsBean> dealsBeans = hzContact.getDeals();
            int size = dealsBeans.size();
            for (int i = 0; i < size; i++) {
                ContactSubItem subItem = new ContactSubItem(dealsBeans.get(i), animatorItem);
                animatorItem.addSubItem(subItem);
            }
            return animatorItem;
        }
        return null;
    }

    @Override
    public boolean isPost() {
        return false;
    }
}
