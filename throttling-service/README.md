# Throttling Service

## About

The service need to limit the requests per second (RPS) for each user.

Requests Per Second, RPS, or r/s is a scalability measure characterizing the throughput handled by a system.

## Services name

* Throttling Service
* Sla Service
* Service

### Throttling Service

The proxy service.

It does [Bandwidth throttling](https://en.wikipedia.org/wiki/Bandwidth_throttling).

https://en.wikipedia.org/wiki/Bandwidth_throttling

#### Implementation #1: [Leaky bucket](https://en.wikipedia.org/wiki/Leaky_bucket)

https://en.wikipedia.org/wiki/Leaky_bucket

The first idea to implement "Leaky bucket" algorithm.

There is a calculation rps before proxy request and after proxy response.

#### Implementation #2: Depends on timestamp

The idea to save requests with a timestamp and manage time on each request.

#### Status

Currently implemented just "Implementation #1".

The code is not covered with tests.

### Sla Service

The service responsible for giving a "Sla" (user, prs) and it depends on Bearer token.

It keeps connections between token and users.

### Service

The service which responsible for data.

The "Throttling Service" protects this service from bad user/hacker actions.

The "Throttling Service" does redirects on this service.

## Diagram

<pre>
                           request to "Throttling Service"
                      /                                       \
               token existed                              token missed
                   /                                            \
         request to "SLA Service"                         unauthorized flow
           /               \                               /             \
      success             failure                    below limit       over limit
        /                    \                          /                  \
  authorized flow       unauthorized flow        proxy "Service"        failure: TooManyRequests
                                                  /         \
                                              success     failure
   (authorized flow | unauthorized flow)
             /              \
       below limit        over limit
          /                   \
   proxy "Service"         failure: TooManyRequests
    /         \
success     failure
</pre>

