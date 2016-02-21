package com.jsfcompref.trainer.entity.accessor;

import com.jsfcompref.trainer.entity.Event;
import com.jsfcompref.trainer.entity.TrainingSession;
import com.jsfcompref.trainer.entity.User;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

@ManagedBean(eager = true)
@ApplicationScoped
public class UserRegistry implements Serializable {

	@PersistenceContext
	EntityManager em;

	@Resource
	UserTransaction usertransaction;

	// <editor-fold defaultstate="collapsed" desc="Accessing and initializing the instance">
	public static UserRegistry getCurrentInstance() {
		UserRegistry result = null;
		Map<String, Object> appMap = FacesContext.getCurrentInstance().
			getExternalContext().getApplicationMap();
		result = (UserRegistry) appMap.get("userRegistry");
		assert (null != result);

		return result;
	}

	@PostConstruct
	public void perApplicationConstructor() {

		try {
			usertransaction.begin();
			Query query = em.createNamedQuery("user.getAll");
			List<User> results = query.getResultList();
			if (results.isEmpty()) {
				populateUsers(em);
				query = em.createNamedQuery("user.getAll");
				results = query.getResultList();
				assert (!results.isEmpty());
			}
			usertransaction.commit();
		} catch (Exception ex) {
			System.err.println("Oops.." + ex);
		}

	}

	private void populateUsers(EntityManager em) {

		// the trainers
		em.persist(new User("Jake", "DeJoque", "male", new java.util.Date(), "jake@vtrainer.com", "Premium", "jake", "jake", true));
		em.persist(new User("Frauke", "Funochel", "female", new java.util.Date(), "frauke@vtrainer.com", "Premium", "frauke", "frauke", true));
		em.persist(new User("Andrew", "Abs", "male", new java.util.Date(), "andrew@vtrainer.com", "Premium", "andrew", "andrew", true));

		// the users
		em.persist(new User("Joe", "Fitness", "male", new java.util.Date(), "jfitness@vtrainer.com", "Premium", "jfitness", "iamcool", false));
		em.persist(new User("Scott", "Tiger", "male", new java.util.Date(), "stiger@vtrainer.com", "Medium", "stiger", "welcome", false));
		em.persist(new User("Karen", "Knees", "female", new java.util.Date(), "karen@vtrainer.com", "Premium", "karen", "karen", false));
		em.persist(new User("Gina", "Glutes", "female", new java.util.Date(), "gina@vtrainer.com", "Basic", "gina", "gina", false));
	}

	// </editor-fold>
	// <editor-fold defaultstate="collapsed" desc="Reading User instances">
	public DataModel<User> getUsers() {
		DataModel<User> users = new ListDataModel<User>(getUserList());
		return users;
	}

	public User getUserByUserid(final String userid) {
		User result = null;

		// PENDING do a query to get this information, don't iterate over
		// the list
		List<User> users = getUserList();
		for (User user : users) {
			if (user.getUserid().equals(userid)) {
				result = user;
				break;
			}
		}

		return result;
	}

	public User getUserById(final Long id) {

		return em.find(User.class, id);

	}

	public List<User> getUserList() {

		Query query = em.createNamedQuery("user.getAll");
		return query.getResultList();

	}

	public List<User> getTrainerList() {

		Query query = em.createNamedQuery("user.getTrainers");
		List<User> results = query.getResultList();
		return results;

	}

	public List<User> getTraineesForTrainer(final User trainer) {

		Query query = em.createNamedQuery("user.getUsersForTrainerId");
		query.setParameter("theId", trainer.getId());
		List<User> results = query.getResultList();
		return results;

	}

	// </editor-fold>
	// <editor-fold defaultstate="collapsed" desc="Writing User instances">
	public void addUser(final User toAdd) throws EntityAccessorException {

		try {
			usertransaction.begin();
			em.persist(toAdd);
			usertransaction.commit();
		} catch (NotSupportedException ex) {
			Logger.getLogger(UserRegistry.class.getName()).log(Level.SEVERE, null, ex);
		} catch (SystemException ex) {
			Logger.getLogger(UserRegistry.class.getName()).log(Level.SEVERE, null, ex);
		} catch (RollbackException ex) {
			Logger.getLogger(UserRegistry.class.getName()).log(Level.SEVERE, null, ex);
		} catch (HeuristicMixedException ex) {
			Logger.getLogger(UserRegistry.class.getName()).log(Level.SEVERE, null, ex);
		} catch (HeuristicRollbackException ex) {
			Logger.getLogger(UserRegistry.class.getName()).log(Level.SEVERE, null, ex);
		} catch (SecurityException ex) {
			Logger.getLogger(UserRegistry.class.getName()).log(Level.SEVERE, null, ex);
		} catch (IllegalStateException ex) {
			Logger.getLogger(UserRegistry.class.getName()).log(Level.SEVERE, null, ex);
		}

	}

	public void updateUser(final User toUpdate) throws EntityAccessorException {

		try {
			usertransaction.begin();
			em.merge(toUpdate);
			usertransaction.commit();
		} catch (Exception ex) {
			System.err.println("Oops. " + ex);
		}

	}

	// </editor-fold>
	// <editor-fold defaultstate="collapsed" desc="Reading TrainingSession instances">
	public List<TrainingSession> getTrainingSessionsForUserAndEvent(final User user,
		final Event e) {

		Query query = em.createNamedQuery("trainingSession.getSessionsForUserAndEvent");
		query.setParameter("theId", user.getId());
		query.setParameter("eventId", e.getId());
		List<TrainingSession> results = query.getResultList();
		return results;

	}

// </editor-fold>
	// <editor-fold defaultstate="collapsed" desc="Writing TrainingSession instances">
	public void addTrainingSessions(final List<TrainingSession> toAdd) throws EntityAccessorException {

		try {
			usertransaction.begin();
			for (TrainingSession t : toAdd) {
				em.persist(t);
			}
			usertransaction.commit();
		} catch (Exception ex) {
			System.err.println("Oops. " + ex);
		}

	}

	public void updateTrainingSession(final TrainingSession toUpdate) throws EntityAccessorException {

		try {
			usertransaction.begin();
			em.merge(toUpdate);
			usertransaction.commit();

		} catch (Exception ex) {
			System.err.println("Oops. " + ex);
		}

	}

	public void removeTrainingSessionForUserAndEvent(final User user,
		final Event e, final TrainingSession trainingSession) {

		try {
			usertransaction.begin();
			em.remove(em.contains(trainingSession) ? trainingSession
				: em.merge(trainingSession)); // em.contains(r) ? r : em.merge(r)
			user.getTrainingSessions().remove(trainingSession);
			em.merge(user);
			usertransaction.commit();

		} catch (Exception ex) {
			System.err.println("Oops. " + ex);
		}

	}

	public void updateTrainingSessionForUser(final User user,
		final TrainingSession trainingSession) {

		try {
			usertransaction.begin();
			em.merge(trainingSession);
			em.merge(user);
			usertransaction.commit();

		} catch (Exception ex) {
			System.err.println("Oops. " + ex);
		}
	}

    // </editor-fold>
}
