package by.fastflow.utils;

import by.fastflow.Ajax;
import by.fastflow.controller.ExceptionHandlerController;
import org.hibernate.Session;

import java.util.Map;

/**
 * Created by KuSu on 27.10.2016.
 */
public abstract class UpdatableDB<T extends UpdatableDB> {

    public abstract void updateBy(T up);

    public Map<String, Object> updateInBDWithToken(Session session, T up, String token) throws RestException {
        validate();
        if (up.havePermissionToModify(session, token)) {
            up.updateBy(this);
            session.beginTransaction();
            session.update(up);
            session.getTransaction().commit();
            session.close();
            return Ajax.successResponse(this);
        } else {
            throw new RestException(ErrorConstants.PERMISSION_BY_TOKEN);
        }
    }

    public abstract void validate() throws RestException;

    public Map<String, Object> updateInBD(Session session, T up) throws RestException {
        validate();
        up.updateBy(this);
        session.beginTransaction();
        session.update(up);
        session.getTransaction().commit();
        session.close();
        return Ajax.successResponse(this);
    }

    public Map<String, Object> delete(Session session, ExceptionHandlerController<T> handler, String token) throws RestException {
        if (havePermissionToModify(session, token)) {
            session.beginTransaction();
            session.delete(this);
            session.getTransaction().commit();
            session.close();
            return Ajax.emptyResponse();
        } else {
            throw new RestException(ErrorConstants.PERMISSION_BY_TOKEN);
        }
    }

    public abstract boolean havePermissionToModify(Session session, String token);

    public abstract T anonimize();
}
