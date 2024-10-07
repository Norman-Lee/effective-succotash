package integrations.turnitin.com.membersearcher.service;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;

import integrations.turnitin.com.membersearcher.client.MembershipBackendClient;
import integrations.turnitin.com.membersearcher.model.MembershipList;

import integrations.turnitin.com.membersearcher.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MembershipService {
	@Autowired
	private MembershipBackendClient membershipBackendClient;

	/**
	 * Method to fetch all memberships with their associated user details included.
	 * This method calls out to the php-backend service and fetches all users and converts that info into a map,
	 * it then calls to fetch all the user memberships and associates them with their corresponding user.
	 *
	 * @return A CompletableFuture containing a fully populated MembershipList object.
	 */
	public CompletableFuture<MembershipList> fetchAllMembershipsWithUsers() {
		return membershipBackendClient.fetchUsers()
				.thenApply(userList -> userList.getUsers().stream().collect(Collectors.toMap(User::getId, Function.identity())
				)).thenCompose( userMap -> membershipBackendClient.fetchMemberships()
                        .thenApply(membershipList -> {
                            membershipList.getMemberships().forEach(member -> member.setUser(userMap.get(member.getUserId())));
                            return membershipList;
                        }));
	}
}
