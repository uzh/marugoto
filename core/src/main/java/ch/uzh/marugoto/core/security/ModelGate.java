package ch.uzh.marugoto.core.security;

import ch.uzh.marugoto.core.data.entity.application.User;

public interface ModelGate {
    public boolean canCreate(User user);
    public boolean canRead(User user, Object objectModel);
    public boolean canUpdate(User user, Object objectModel);
    public boolean canDelete(User user, Object objectModel);
}
