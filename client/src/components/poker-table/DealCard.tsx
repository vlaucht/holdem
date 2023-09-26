

// TODO - Refactor this file: there is a lot of duplicate code that needs to be cleaned up


/**
 * This function animates the dealing of the hole cards to each player.
 *
 * <p>
 *     The animation starts from the dealer's seat and moves clockwise.
 *     The animation is repeated for each round of dealing.
 * </p>
 *
 * @param game The current game state
 * @param seatElements The list of seat elements
 * @param flyingCardRef The reference to the animated card
 * @param dealerIndex The index of the dealer in the player list
 * @param numRounds The number of rounds to deal
 * @param callback The callback function to be called after the animation is complete
 */
export function animateSeatCardFly(game: { players: any[]; }, seatElements: any[], flyingCardRef: { current: any; },
                               dealerIndex: number, numRounds: number, callback?: () => void) {
    const totalPlayers = game.players.length;
    let currentIndex = dealerIndex; // Start animation from the dealer
    let roundsDealt = 0;

    const dealerSeat = seatElements[dealerIndex];
    const flyingCard = flyingCardRef.current;


    // Set the initial position of the flying card to match the dealer's seat
    if (dealerSeat && flyingCard) {
        const dealerSeatRect = dealerSeat.getBoundingClientRect();
        flyingCard.style.left = `${dealerSeatRect.left}px`;
        flyingCard.style.top = `${dealerSeatRect.top}px`;

        // Toggle visibility to make the flying card visible
        flyingCard!.style.visibility = "visible";
    }


    function animateToNextSeat() {
        if (roundsDealt < numRounds) {
            const nextIndex = (currentIndex + 1) % totalPlayers;
            const nextSeat = seatElements[nextIndex];

            if (nextSeat && flyingCard) {
                // Calculate the animation transform relative to the dealer's seat
                const dealerSeatRect = dealerSeat!.getBoundingClientRect();
                const nextSeatRect = nextSeat.getBoundingClientRect();

                const translateX = nextSeatRect.left - dealerSeatRect.left;
                const translateY = nextSeatRect.top - dealerSeatRect.top;

                // Calculate the animation duration based on distance
                const distance = Math.sqrt(translateX ** 2 + translateY ** 2);
                const duration = distance / 600; // Adjust animation speed as needed (faster)

                // Apply the animation transform
                flyingCard.style.transition = `transform ${duration}s ease-in-out`;
                flyingCard.style.transform = `translate(${translateX}px, ${translateY}px)`;

                // Reset the card position after the animation
                setTimeout(() => {
                    flyingCard.style.transition = "none";
                    flyingCard.style.transform = "translate(0, 0)";
                    const holeCard = nextSeat.querySelector(`.hole-card${roundsDealt+1}`) as HTMLElement;
                    if (holeCard) {
                        // Modify the visibility of the hole card
                        holeCard.style.visibility = "visible";
                    }
                }, duration * 1000);

                // Move to the next player
                currentIndex = nextIndex;

                // Check if we've completed one round
                if (currentIndex === dealerIndex) {
                    roundsDealt++;
                }

                // Trigger the next animation
                setTimeout(animateToNextSeat, duration * 1000);
            }
        } else {
            // because we started at the player after the dealer, we need to show the dealer's hole card manually
            dealerSeat!.querySelector(".hole-card1")!.style.visibility = "visible";
            // After all cards are dealt, hide the flying card
            flyingCard!.style.visibility = "hidden";

            if (callback) {
                callback();
            }

        }
    }

    // Start the animation
    setTimeout(animateToNextSeat, 100); // Start after 0.1 seconds
}

/**
 * This function animates the dealing of the board cards.
 *
 * <p>
 *     The animation starts from the dealer's seat.
 * </p>
 *
 * @param dealerSeat The dealer's seat element
 * @param boardCardRefs The list of board card refs
 * @param flyingCardRef The reference to the animated card
 */
export function animateBoardCardFly(dealerSeat: HTMLElement, boardCardRefs: any[], flyingCardRef: { current: any; }) {
    const numBoardCards = boardCardRefs.length;
    let currentCardIndex = 0; // Start animation from the first board card

    const flyingCard = flyingCardRef.current;

    if (dealerSeat && flyingCard) {
        const dealerSeatRect = dealerSeat.getBoundingClientRect();
        flyingCard.style.left = `${dealerSeatRect.left}px`;
        flyingCard.style.top = `${dealerSeatRect.top}px`;

        // Toggle visibility to make the flying card visible
        flyingCard.style.visibility = "visible";
    }

    function animateToNextBoardCard() {
        if (currentCardIndex < numBoardCards) {
            const nextCard = boardCardRefs[currentCardIndex].current;
            if (nextCard && flyingCard) {
                const nextCardRect = nextCard.getBoundingClientRect();

                // Calculate the animation transform relative to the dealer seat
                const dealerSeatRect = dealerSeat.getBoundingClientRect();
                const translateX = nextCardRect.left - dealerSeatRect.left;
                const translateY = nextCardRect.top - dealerSeatRect.top;

                // Calculate the animation duration based on distance
                const distance = Math.sqrt(translateX ** 2 + translateY ** 2);
                const duration = distance / 600; // Adjust animation speed as needed (faster)

                // Apply the animation transform
                flyingCard.style.transition = `transform ${duration}s ease-in-out`;
                flyingCard.style.transform = `translate(${translateX}px, ${translateY}px)`;

                // Reset the card position after the animation
                setTimeout(() => {
                    flyingCard.style.transition = "none";
                    flyingCard.style.transform = "translate(0, 0)";
                    // Modify the visibility of the board card
                    const boardCard = document.querySelector(`.board-card${currentCardIndex-1}`) as HTMLElement;
                    if (boardCard) {
                        boardCard.style.visibility = "visible";
                    }
                }, duration * 1000);



                // Move to the next board card
                currentCardIndex++;

                // Trigger the next animation
                setTimeout(animateToNextBoardCard, duration * 1000);
            }
        } else {
            // After all board cards are revealed, hide the flying card
            if (flyingCard) {
                flyingCard.style.visibility = "hidden";
            }
        }
    }

    // Start the animation
    setTimeout(animateToNextBoardCard, 100); // Start after 0.1 seconds
}

export const toggleHoleCardVisibility = (isVisible: boolean) => {
    const holeCardElements = document.querySelectorAll(".hole-card1, .hole-card2");
    holeCardElements.forEach((element) => {
        (element as HTMLElement).style.visibility = isVisible ? "visible" : "hidden";
    });
};

export const toggleBoardCardVisibility = (isVisible: boolean) => {
    const boardCardElements = document.querySelectorAll(".board-card0, .board-card1, .board-card2, .board-card3, .board-card4");
    boardCardElements.forEach((element) => {
        (element as HTMLElement).style.visibility = isVisible ? "visible" : "hidden";
    });
}