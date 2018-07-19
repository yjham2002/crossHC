package kr.co.picklecode.crossmedia.hiddencatch.model;

import java.util.List;

/**
 * Created by HP on 2018-07-19.
 */

public interface DBCall<T extends DBox> {
    void fire(List<T> dBoxList);
}
