(() => {
    
    const LOL_RANK = {
        IRON: 1,
        BRONZE: 2,
        SILVER: 3,
        GOLD: 4,
        PLATINUM: 5,
        DIAMOND: 6,
        MASTER: 7,
        GRANDMASTER: 8,
        CHALLENGER: 9,
        I: 0.75,
        II: 0.5,
        III: 0.25,
        IV: 0
    };
    const BASE_URL = './search';
    const MAX_SUMMONER_COUNT = 4;
    const SUMMONER_ITEMS = [];
    let sortBy = 'rank';
    
    const textarea = document.querySelector('textarea');
    const submitButton = document.querySelector('#submit-btn');
    const selection = document.querySelector('select');
    const toast = document.querySelector('#toast');
    const toastButton = document.querySelector('#toast-btn');
    const spinner = document.querySelector('#spinner');
    
    /**
     * AJAX helper
     *
     * @param method - GET|POST|PUT|DEvarE
     * @param url - API end point
     * @param data - request payload data
     * @param successCallback - Successful callback function
     * @param errorCallback - Error callback function
     */
    const ajax = (method, url, data, successCallback, errorCallback) => {
        const xhr = new XMLHttpRequest();

        xhr.open(method, url, true);
        xhr.onload = function() {
            if (xhr.status === 200) successCallback(xhr.responseText);
            else errorCallback();
        };
        xhr.onerror = function() {
            console.error("The request couldn't be compared.");
            errorCallback();
        };
        if (data === null) xhr.send();
        else {
            xhr.setRequestHeader("Content-Type", "application/json;charset=utf-8");
            xhr.send(data);
        }
    }
    
    const toggleToast = (message, display) => {
        toast.children[0].children[0].innerHTML = message;
	    toast.style = `display: ${display};`;
    }
    
    const load = () => {
        const tbody = document.querySelector('tbody');
        while (tbody.firstChild) tbody.removeChild(tbody.firstChild);
        toggleToast('', 'none !important;');
        spinner.style = "display: block;";
    }
    
    const unload = () => {
        spinner.style = "display: none !important;";
    }
    
    const getSummonersFromInput = (rawInput) => {
    
        let input = rawInput.replaceAll('\n', ' ').replaceAll('.', ' ').replaceAll('joined the room', 'joined').replaceAll('joined the lobby', 'joined').trim();
        //if (!ONLY_NUMBER_LETTER_REGEX.test(input)) throw new Error(`${rawInput} contains an invalid character.`);
        let summoners = [];
        let summonerCount = 0;
        
        while (input.length > 0 && summonerCount < MAX_SUMMONER_COUNT) {
            const startIndex = 0;
            const endIndex = input.indexOf('joined');
            let summoner_name;
            if (endIndex != -1) {
                summoner_name = input.substring(startIndex, endIndex).trim();
                input = input.substring(endIndex + 'joined'.length);
            } else {
                summoner_name = input.trim();
                input = '';
            }
            const found = summoners.find(item => { 
              return item['summoner_name'] == summoner_name
            });
            if (!found) {
                summoners.push(
                  {
                    'summoner_name': summoner_name
                  }
                );
                summonerCount++;
            }
        }
        return summoners;
    }
    
    const getSortedItems = () => {
    	if (sortBy == 'rank') {
    		return SUMMONER_ITEMS.sort((a, b) => {
    		    const rankArrA = a.rank.split(' ');
    		    const rankArrB = b.rank.split(' ');	    
    		    const rankA = LOL_RANK[rankArrA[0]] + LOL_RANK[rankArrA[1]];
    		    const rankB = LOL_RANK[rankArrB[0]] + LOL_RANK[rankArrB[1]];
    		    if (rankA == rankB) return parseInt(rankArrB[2]) - parseInt(rankArrA[2]);
    		    return rankB - rankA;
    		});
    	}
    	return SUMMONER_ITEMS.sort((a, b) => {
	        const winRateA = parseFloat(a[sortBy].substring(0, a[sortBy].indexOf('%')));
	        const winRateB = parseFloat(b[sortBy].substring(0, b[sortBy].indexOf('%')));
	        return winRateB - winRateA;
	    });
    }
    
    const setRankingTable = () => {
        const items = getSortedItems();
        const tbody = document.querySelector('tbody');
        while (tbody.firstChild) tbody.removeChild(tbody.firstChild);
        
        for (let i = 0; i < items.length; i++) {
		    const item = items[i];
		    const tr = document.createElement('tr');
		    
		    const name = document.createElement('th');
	        name.setAttribute('scope', 'row');
	        name.innerHTML = item.playerName;
	        tr.appendChild(name);
	        
	        const rankTd = document.createElement('td');
	        rankTd.innerHTML = item.rank;
	        tr.appendChild(rankTd);
	        
	        const recentWRTd = document.createElement('td');
	        recentWRTd.innerHTML = item.recentWrKda;
	        tr.appendChild(recentWRTd);
	        
	        const overallWRTd = document.createElement('td');
	        overallWRTd.innerHTML = item.overAllWr;
	        tr.appendChild(overallWRTd);
	        
	        const winLossTd = document.createElement('td');
	        winLossTd.innerHTML = item['win/loss'];
	        tr.appendChild(winLossTd);
	        
	        if (item.hasOwnProperty('dodgeWarning') && item.dodgeWarning) {
	            name.className = 'text-danger';
	            rankTd.className = 'text-danger';
	            recentWRTd.className = 'text-danger';
	            overallWRTd.className = 'text-danger';
	            winLossTd.className = 'text-danger';
	            const toastMessage = `DODGE WARNING:<br> At least one bad player's been detected, it's highly recommended to skip this match.`;
	            toggleToast(toastMessage, 'block');
	        }
	        tbody.appendChild(tr);
		}
    }
    
    submitButton.addEventListener('click', () => {
        try {
            const summoners = getSummonersFromInput(textarea.value);
            if (summoners.length < 1) return;
            SUMMONER_ITEMS.length = 0;
            console.log(SUMMONER_ITEMS);
            const url = BASE_URL;
            const req = JSON.stringify(summoners);
            return new Promise((resolve) => {
		        load();
		        ajax('POST', url, req, (res) => {
		            const items = JSON.parse(res);
		            if (items && items instanceof Array && items.length > 0) resolve(items);
		            else throw new Error('The response body does not contain a valid array.');
		        }, (error) => { console.error(error.stack) });
            })
            .then((items) => {
		        for (const item of items) {
		            if (item.errorMessage != '') {
		                return {
		                    playerName: item.playerName,
		                    errorMessage: item.errorMessage
		                };
		            }
		        SUMMONER_ITEMS.push(item);
		        }
            })
            .then((errorItem) => {
                if (errorItem) {
                    const toastMessage = `ERROR:<br> ${errorItem.playerName} ${errorItem.errorMessage}.`;
                    toggleToast(toastMessage, 'block');
                } else {
                    setRankingTable();
                }
                unload();
            });
        } catch (e) {
            console.error(e.stack);
        }
      }
    );
    
    selection.addEventListener('change', () => {
        if (sortBy != selection.value) {
            sortBy = selection.value;
            if (SUMMONER_ITEMS.length > 0) {
                load();
	            setRankingTable();
	            unload();
            }
        }
    });
    
    toastButton.addEventListener('click', () => {
        toggleToast('', 'none !important;');
    });
})();