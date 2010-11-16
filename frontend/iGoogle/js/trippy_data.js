/**
 * @fileoverview Class for Gulliver gadget of type JGulliverData.
 * @author
 */

/**
 * Class defines singleton object for itinerary.
 * @constructor
 */
function JGulliverData() {
  this.nSelectedTripIndex = -1;
  this.arrTripData = [];
}

/**
 * Instance of JGulliverData class.
 * @type {JGulliverData}.
 * @constructor
 */
JGulliverData.instance = null;

/**
 * It returns the instance of itinerary if exists else create new
 * instance.
 * @return {Object} object of JGulliverData.
 */
JGulliverData.getInstance = function() {
  if (!JGulliverData.instance) {
    JGulliverData.instance = new JGulliverData();
  }
  return JGulliverData.instance;
};

/**
 * Creates a trip object and returns it.
 * @return {Object} Returns trip object.
 */
function getTripObject() {
  return {
    id: '',
    ownerId: '',
    ownerName: '',
    name: '',
    loc: '',
    lat: '',
    lng: '',
    accuracy: 0,
    zoomLevel: 12,
    duration: 7,
    sdate: '',
    fdate: '',
    thumbUp: 0,
    thumbDown: 0
  };
}

/**
 * Different categories of google search.
 * @type {Array}
 */
var categories = {
  'Aparthotels': {
    'name': 'Aparthotels',
    'type': 'Lodging'
  },
  'Apartments': {
    'name': 'Apartments',
    'type': 'Lodging'
  },
  'Apartments/Condos': {
    'name': 'Apartments/Condos',
    'type': 'Lodging'
  },
  'Bed & Breakfasts': {
    'name': 'Bed & Breakfasts',
    'type': 'Lodging'
  },
  'Cabins/Chalets': {
    'name': 'Cabins/Chalets',
    'type': 'Lodging'
  },
  'Campgrounds/RV Parking': {
    'name': 'Campgrounds/RV Parking',
    'type': 'Lodging'
  },
  'Camping': {
    'name': 'Campings',
    'type': 'Lodging'
  },
  'Cottages': {
    'name': 'Cottages',
    'type': 'Lodging'
  },
  'Extended Stay': {
    'name': 'Extended Stay',
    'type': 'Lodging'
  },
  'Farmstays': {
    'name': 'Farmstays',
    'type': 'Lodging'
  },
  'Guest Houses': {
    'name': 'Guest Houses',
    'type': 'Lodging'
  },
  'Hostels': {
    'name': 'Hostels',
    'type': 'Lodging'
  },
  'Hotels': {
    'name': 'Hotels',
    'type': 'Lodging'
  },
  'Inns': {
    'name': 'Inns',
    'type': 'Lodging'
  },
  'Lodges': {
    'name': 'Lodges',
    'type': 'Lodging'
  },
  'Motels': {
    'name': 'Motels',
    'type': 'Lodging'
  },
  'Paradors': {
    'name': 'Paradors',
    'type': 'Lodging'
  },
  'Pensions': {
    'name': 'Pensions',
    'type': 'Lodging'
  },
  'Ranches': {
    'name': 'Ranches',
    'type': 'Lodging'
  },
  'Resorts': {
    'name': 'Resorts',
    'type': 'Lodging'
  },
  'Vacation Homes': {
    'name': 'Vacation Homes',
    'type': 'Lodging'
  },
  'Villas': {
    'name': 'Villas',
    'type': 'Lodging'
  },
  'After-hours': {
    'name': 'After-hours',
    'type': 'Nightlife'
  },
  'Arcades': {
    'name': 'Arcades',
    'type': 'Nightlife'
  },
  'Bars': {
    'name': 'Bars',
    'type': 'Nightlife'
  },
  'Beer Gardens': {
    'name': 'Beer Gardens',
    'type': 'Nightlife'
  },
  'Cabarets': {
    'name': 'Cabarets',
    'type': 'Nightlife'
  },
  'Cafés': {
    'name': 'Cafés',
    'type': 'Nightlife'
  },
  'Casinos': {
    'name': 'Casinos',
    'type': 'Nightlife'
  },
  'Cigar Bars': {
    'name': 'Cigar Bars',
    'type': 'Nightlife'
  },
  'Cinemas': {
    'name': 'Cinemas',
    'type': 'Nightlife'
  },
  'Classical Music/Opera': {
    'name': 'Classical Music/Opera',
    'type': 'Nightlife'
  },
  'Comedy': {
    'name': 'Comedy',
    'type': 'Nightlife'
  },
  'Dance': {
    'name': 'Dance',
    'type': 'Nightlife'
  },
  'Dance Clubs': {
    'name': 'Dance Clubs',
    'type': 'Nightlife'
  },
  'Gay & Lesbian': {
    'name': 'Gay & Lesbian',
    'type': 'Nightlife'
  },
  'Gentlemen\'s Clubs': {
    'name': 'Gentlemen\'s Clubs',
    'type': 'Nightlife'
  },
  'Ice Cream': {
    'name': 'Ice Cream',
    'type': 'Nightlife'
  },
  'Jazz/Blues': {
    'name': 'Jazz/Blues',
    'type': 'Nightlife'
  },
  'Karaoke': {
    'name': 'Karaoke',
    'type': 'Nightlife'
  },
  'Late Night Dining': {
    'name': 'Late Night Dining',
    'type': 'Nightlife'
  },
  'Live Music': {
    'name': 'Live Music',
    'type': 'Nightlife'
  },
  'Lounges': {
    'name': 'Lounges',
    'type': 'Nightlife'
  },
  'Luau': {
    'name': 'Luau',
    'type': 'Nightlife'
  },
  'Nightclubs': {
    'name': 'Nightclubs',
    'type': 'Nightlife'
  },
  'Offbeat Nightlife': {
    'name': 'Offbeat Nightlife',
    'type': 'Nightlife'
  },
  'Pool Halls': {
    'name': 'Pool Halls',
    'type': 'Nightlife'
  },
  'Pubs': {
    'name': 'Pubs',
    'type': 'Nightlife'
  },
  'Sports Bars': {
    'name': 'Sports Bars',
    'type': 'Nightlife'
  },
  'Tapas': {
    'name': 'Tapas',
    'type': 'Nightlife'
  },
  'Tea Houses': {
    'name': 'Tea Houses',
    'type': 'Nightlife'
  },
  'Theater': {
    'name': 'Theater',
    'type': 'Nightlife'
  },
  'Venues': {
    'name': 'Venues',
    'type': 'Nightlife'
  },
  'Wine Bars': {
    'name': 'Wine Bars',
    'type': 'Nightlife'
  },
  '4WD': {
    'name': '4WD',
    'type': 'See & Do'
  },
  'Adventure': {
    'name': 'Adventure',
    'type': 'See & Do'
  },
  'Air Tours': {
    'name': 'Air Tours',
    'type': 'See & Do'
  },
  'Architecture': {
    'name': 'Architecture',
    'type': 'See & Do'
  },
  'Art Museums/Galleries': {
    'name': 'Art Museums/Galleries',
    'type': 'See & Do'
  },
  'Attractions': {
    'name': 'Attractions',
    'type': 'See & Do'
  },
  'Baths': {
    'name': 'Baths',
    'type': 'See & Do'
  },
  'Beaches': {
    'name': 'Beaches',
    'type': 'See & Do'
  },
  'Bike Rentals': {
    'name': 'Bike Rentals',
    'type': 'See & Do'
  },
  'Biking': {
    'name': 'Biking',
    'type': 'See & Do'
  },
  'Birding': {
    'name': 'Birding',
    'type': 'See & Do'
  },
  'Boating': {
    'name': 'Boating',
    'type': 'See & Do'
  },
  'Breweries': {
    'name': 'Breweries',
    'type': 'See & Do'
  },
  'Camel Rides': {
    'name': 'Camel Rides',
    'type': 'See & Do'
  },
  'Campgrounds': {
    'name': 'Campgrounds',
    'type': 'See & Do'
  },
  'Canoe/Kayak/Rafting': {
    'name': 'Canoe/Kayak/Rafting',
    'type': 'See & Do'
  },
  'Casinos': {
    'name': 'Casinos',
    'type': 'See & Do'
  },
  'Castles , Palaces & Historic Buildings': {
    'name': 'Castles , Palaces & Historic Buildings',
    'type': 'See & Do'
  },
  'Caves': {
    'name': 'Caves',
    'type': 'See & Do'
  },
  'Climbing': {
    'name': 'Climbing',
    'type': 'See & Do'
  },
  'Cooking Classes': {
    'name': 'Cooking Classes',
    'type': 'See & Do'
  },
  'Culture': {
    'name': 'Culture',
    'type': 'See & Do'
  },
  'Diving and Snorkeling': {
    'name': 'Diving and Snorkeling',
    'type': 'See & Do'
  },
  'Drives': {
    'name': 'Drives',
    'type': 'See & Do'
  },
  'Educational': {
    'name': 'Educational',
    'type': 'See & Do'
  },
  'Equestrian': {
    'name': 'Equestrian',
    'type': 'See & Do'
  },
  'Events': {
    'name': 'Events',
    'type': 'See & Do'
  },
  'Farms': {
    'name': 'Farms',
    'type': 'See & Do'
  },
  'Ferries': {
    'name': 'Ferries',
    'type': 'See & Do'
  },
  'Fishing': {
    'name': 'Fishing',
    'type': 'See & Do'
  },
  'Free': {
    'name': 'Free',
    'type': 'See & Do'
  },
  'Golf': {
    'name': 'Golf',
    'type': 'See & Do'
  },
  'Hiking': {
    'name': 'Hiking',
    'type': 'See & Do'
  },
  'Horseback Riding': {
    'name': 'Horseback Riding',
    'type': 'See & Do'
  },
  'Lakes and Rivers': {
    'name': 'Lakes and Rivers',
    'type': 'See & Do'
  },
  'Landmarks': {
    'name': 'Landmarks',
    'type': 'See & Do'
  },
  'Markets': {
    'name': 'Markets',
    'type': 'See & Do'
  },
  'Museums': {
    'name': 'Museums',
    'type': 'See & Do'
  },
  'National Parks': {
    'name': 'National Parks',
    'type': 'See & Do'
  },
  'Nature & Wildlife': {
    'name': 'Nature & Wildlife',
    'type': 'See & Do'
  },
  'Offbeat Activities': {
    'name': 'Offbeat Activities',
    'type': 'See & Do'
  },
  'Outdoor Recreation': {
    'name': 'Outdoor Recreation',
    'type': 'See & Do'
  },
  'Picnics, Parks & Gardens': {
    'name': 'Picnics, Parks & Gardens',
    'type': 'See & Do'
  },
  'Pyramids and Tombs': {
    'name': 'Pyramids and Tombs',
    'type': 'See & Do'
  },
  'Recreation & Natural Areas': {
    'name': 'Recreation & Natural Areas',
    'type': 'See & Do'
  },
  'Religious Sights': {
    'name': 'Religious Sights',
    'type': 'See & Do'
  },
  'Shopping': {
    'name': 'Shopping',
    'type': 'See & Do'
  },
  'Shows': {
    'name': 'Shows',
    'type': 'See & Do'
  },
  'Spas': {
    'name': 'Spas',
    'type': 'See & Do'
  },
  'Sports': {
    'name': 'Sports',
    'type': 'See & Do'
  },
  'Tours': {
    'name': 'Tours',
    'type': 'See & Do'
  },
  'Trains': {
    'name': 'Trains',
    'type': 'See & Do'
  },
  'Transfers': {
    'name': 'Transfers',
    'type': 'See & Do'
  },
  'Visitor Centers': {
    'name': 'Visitor Centers',
    'type': 'See & Do'
  },
  'Walking': {
    'name': 'Walking',
    'type': 'See & Do'
  },
  'Water Sports': {
    'name': 'Water Sports',
    'type': 'See & Do'
  },
  'Wedding Chapels': {
    'name': 'Wedding Chapels',
    'type': 'See & Do'
  },
  'Wineries': {
    'name': 'Wineries',
    'type': 'See & Do'
  },
  'Winter Sports': {
    'name': 'Winter Sports',
    'type': 'See & Do'
  },
  'Zoos/Aquariums': {
    'name': 'Zoos/Aquariums',
    'type': 'See & Do'
  }
};
